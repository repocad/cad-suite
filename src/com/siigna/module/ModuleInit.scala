/*
 * Copyright (c) 2012. Siigna is released under the creative common license by-nc-sa. You are free
 * to Share — to copy, distribute and transmit the work,
 * to Remix — to adapt the work
 *
 * Under the following conditions:
 * Attribution —  You must attribute the work to http://siigna.com in the manner specified by the author or licensor (but not in any way that suggests that they endorse you or your use of the work).
 * Noncommercial — You may not use this work for commercial purposes.
 * Share Alike — If you alter, transform, or build upon this work, you may distribute the resulting work only under the same or similar license to this one.
 */

package com.siigna.module

import base.Menu
import base.radialmenu.category.StartCategory
import com.siigna._
import app.model.shape.{FullSelector, ShapeSelector}
import java.awt.Color
import com.siigna.module.base.paperHeader


/**
 * An init module for the cad-suite.
 */
class ModuleInit extends Module {
  println("INIT")
  Menu.startCategory = StartCategory

  protected var lastModule : Option[ModuleInstance] = None

  //The nearest shape to the current mouse position.
  var nearestShape : Option[(Int, Shape)] = None

  //Check if there is a useable selection:
  // TODO: Make a more elegant way to check for usable selection - in mainline?
  def usableSelectionExists = {
    var usableSelectionExists: Boolean = false
    if (!Drawing.selection.isEmpty) {
      //The selection could be an empty map, which is unusable - check for that:
      if (Drawing.selection.get.self.size != 0)
      //The map could contain an EmptySelector or a Selector with
      // an empty bit-set, which is unusable - check for that:
        Drawing.selection.get.self.foreach((shape) => {
          shape._2 match {
            //A FullSelector or a selector containing a BitSet means a useable selection:
            case FullSelector => usableSelectionExists = true
            case app.model.shape.PolylineShape.Selector(x) => {
              if (x.size >0) {
                //If the size of the bitset is larger than 0, something useful is selected...
                usableSelectionExists = true
              }
            }
            case app.model.shape.LineShape.Selector(x) => {
              //If the selector exists, something useful is selected...
              usableSelectionExists = true
            }
            case app.model.shape.CircleShape.Selector(x) => {
              //If the selector exists, something useful is selected...
              usableSelectionExists = true
            }
            case app.model.shape.GroupShape.Selector(x) => if (x.size >0) {
              //If the bitset is larger than 0, something useful is selected...
              usableSelectionExists = true
            }
            case _ =>
          }
        })
    }
    (usableSelectionExists)
  }

  def stateMap = Map(
    'Start -> {
      // Match for modules to forward to
      case End(module : ModuleInstance) :: tail => {
        lastModule = Some(module) // Store it as a last module
        Start(module) // Forward
      }

      //Rightclick starts menu:
      case MouseDown(_, MouseButtonRight, _) :: tail => Start('Menu, "com.siigna.module.base")

      //double click anywhere on a shape selects the full shape.
      case  MouseDown(p2, button, modifier) :: MouseUp(p1 ,MouseButtonLeft , _) :: tail => {
        if (p1 == p2){
          Start('Selection, "com.siigna.module.base", MouseDouble(p2,button,modifier))
        } }

      //Leftclick:
      // 1: Starts select, to select shape part at cursor, if nothing is selected,
      // TODO: or if the click was away from the selection:
      case MouseDown(p, MouseButtonLeft, modifier) :: tail => {
        //Check if there is a useable selection:
        Start('Selection, "com.siigna.module.base", MouseDown(p, MouseButtonLeft, modifier))
      }

      //Leftclick and drag starts :
      // 1: Move if something is selected, and near where the drag starts, or
      // 2: Select, if nothing is selected yet, or if something is selected, but away from the cursor
      // TODO: Change it, so it is if something is not close to the selection - now it if the click
      // is close to any shape in the drawing
      case MouseDrag(p2, button2, modifier2) :: MouseDown(p, button, modifier) :: tail => {
        if (usableSelectionExists == true) {
          val m = p.transform(View.deviceTransformation)
          if (Drawing(m).size > 0) {
            val nearest = Drawing(m).reduceLeft((a, b) => if (a._2.geometry.distanceTo(m) < b._2.geometry.distanceTo(m)) a else b)
            if (nearest._2.distanceTo(m) < Siigna.selectionDistance) {
            Start('Move, "com.siigna.module.base.modify", p )
            } else {
            Start('Selection, "com.siigna.module.base", MouseDrag(p, button, modifier))
          }} else {
            //Necessary since Drawing(m).size is 0 when marking far away from selected shape/parts
            Start('Selection, "com.siigna.module.base", MouseDrag(p, button, modifier))
          }
        } else {
          Start('Selection, "com.siigna.module.base", MouseDrag(p, button, modifier))
        }
      }

      // Delete
      case KeyDown(Key.Delete, _) :: tail => {
        if (usableSelectionExists) {
          Delete(Drawing.selection.get.self)
        }
      }

      //shortcuts
      case KeyDown('c', _) :: KeyUp('p', _) :: tail => {
        Start('Colors, "com.siigna.module.base.properties")
      }
      case KeyDown('l', _) :: KeyUp('c', _) :: tail => {
        Start('Line, "com.siigna.module.base.create")
      }
      case KeyDown('p', _) :: KeyUp('c', _) :: tail => {
        Start('Polyline, "com.siigna.module.base.create")
      }
      case KeyDown('c', _) :: KeyUp('c', _) :: tail => {
        Start('Circle, "com.siigna.module.base.create")
      }

      case KeyDown('a', Control) :: tail => Drawing.selectAll()
      case KeyDown('z', Control) :: tail => Drawing.undo()
      case KeyDown('y', Control) :: tail => Drawing.redo()

      // Forward to the last initiated module
      case KeyDown(Key.Space, _) :: tail => if (lastModule.isDefined) Start(lastModule.get.copy)

      // Release all selections
      case KeyDown(Key.Esc, _) :: tail => Drawing.deselect()
      case _ =>
    }
  )
  override def paint(g : Graphics, t : TransformationMatrix) {

    val headerShapes = new paperHeader

    //val transformation = headerShapes.transformation
    val scale = headerShapes.scale
    //val unitX = headerShapes.unitX(4)

    g draw headerShapes.openness(t) //draw frame to indicate level of openness:
    g draw headerShapes.horizontal(t) // Draw horizontal headerborder
    g draw headerShapes.vertical(t) //Draw vertical headerborder
    //g.draw(headerShapes.getURL.transform(transformation.translate(scale.boundary.topRight + unitX))) //g draw separator
    g.draw(scale.transform(headerShapes.transformation(t)))   //draw paperScale

    //draw highlighted vertices and segments that are selectable (close to the mouse)
    if (nearestShape.isDefined) {
      val shape  = nearestShape.get._2
      val part = shape.getPart(mousePosition)
      val points = shape.getVertices(part)
      points.foreach(p => g.draw(t.transform(p)))

      //TODO: activate this -> implement adding attributes to parts in mainline
      //g draw part.setAttributes("Color" -> "#22FFFF".color, "StrokeWidth" -> 1.0).transform(t)
    }
  }
}
