/*
 * Copyright (c) 2008-2013, Selftie Software. Siigna is released under the
 * creative common license by-nc-sa. You are free
 *   to Share — to copy, distribute and transmit the work,
 *   to Remix — to adapt the work
 *
 * Under the following conditions:
 *   Attribution —   You must attribute the work to http://siigna.com in
 *                    the manner specified by the author or licensor (but
 *                    not in any way that suggests that they endorse you
 *                    or your use of the work).
 *   Noncommercial — You may not use this work for commercial purposes.
 *   Share Alike   — If you alter, transform, or build upon this work, you
 *                    may distribute the resulting work only under the
 *                    same or similar license to this one.
 *
 * Read more at http://siigna.com and https://github.com/siigna/main
 */

package com.siigna.module.cad.modify

import com.siigna._
import module.cad.create.InputRequestNew

class Trim extends Module {

  private var attr = Attributes()

  //the shape to be trimmed
  private var nearestShape : Option[Shape] = None

  val stateMap: StateMap = Map(
    //check if shapes are selected. If not, allow the user to do so.
    'Start -> {
      //exit strategy
      case KeyDown(Key.Esc, _) :: tail => End

      //create testshapes
      case KeyDown(Key.ArrowDown, _) :: tail => {
        val lineVert = List(Vector2D(-100,0),Vector2D(10,10), Vector2D(100,0))
        val lineHoriz1 = List(Vector2D(-10,20),Vector2D(-15,-30))
        val lineHoriz2 = List(Vector2D(10,20),Vector2D(10,-30))

        Create(PolylineShape(lineVert))
        Create(PolylineShape(lineHoriz1))
        Create(PolylineShape(lineHoriz2))
      }

      //save the selection if returned from the select module
      case End(MouseDown(p, MouseButtonLeft, _)) :: tail => {
        //Dont know what this is / Niels

      }

      case _ => {
        //Go to trim - state only if there is a selection
        println("Underscore")
        if(Drawing.selection.isEmpty) {
          Siigna display "No shapes selected - select shapes to trim"
          println("No selection")
          //End
        } else {
          println(" selection")
          'Trim
        }
      }

    },

    //when shapes are selected, check for mouse clicks to trim shapes. (TODO: trim by selection box)
    'Trim -> {
      case End(p : Vector2D) :: tail =>
      case MouseUp(p, _, _) :: tail => {
        val t = View.deviceTransformation
        val point = p.transform(t)
        val nearest = Drawing(point).reduceLeft((a, b) => if (a._2.geometry.distanceTo(point) < b._2.geometry.distanceTo(point)) a else b)
        val trimLine = if (nearest._2.distanceTo(point) < Siigna.selectionDistance) Some(nearest._2) else None
        //attr = trimLine.get.attributes
        println("trimLine: "+trimLine.get)
        if(trimLine.isDefined) {
          println("running trimming method")
          TrimmingMethods.trimPolyline(Drawing.selection.shapes,trimLine.get,point)
        }
      }

      //exit strategy
      case KeyDown(Key.Esc, _) :: tail => End
      case MouseDown(p, MouseButtonRight, _) :: tail => End
      case End(KeyDown(Key.Esc, _)) :: tail => End


      case e => {
        println("error: "+e)
        Siigna display "Click shapes to trim"
        //Requests mouse-down input
        Start('cad,"create.InputNew",InputRequestNew(6,None))
      }
    }
  )
}