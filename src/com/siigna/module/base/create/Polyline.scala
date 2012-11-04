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

package com.siigna.module.base.create

import com.siigna._
import app.Siigna
import java.awt.Color

class Polyline extends Module {

  var startPoint: Option[Vector2D] = None
  private var points   = List[Vector2D]()
  var attributes : Attributes = Attributes()
  def set(name : String, attr : String) = Siigna.get(name).foreach((p : Any) => attributes = attributes + (attr -> p))

  // The polylineshape so far
  private var shape : Option[PolylineShape] = None

  val stateMap: StateMap = Map(
    'Start -> {
      case End(v : Vector2D) :: tail => {
        //Hvis status fra returnerende enhed er END, er der besked tilbage fra point med et nyt punkt.
        println("Starter polyline-modul, med END")
        points = points :+ v
        if (startPoint.isEmpty){
          //Hvis startpunktet ikke er sat, er det første polylineshapedel, vi er i gang med at tegne.
          startPoint = Some(v)
          val guide : Guide = Guide((v : Vector2D) => {
            Array(PolylineShape(startPoint.get, v))
          })
          Start('Point,"com.siigna.module.base.create", guide)
        } else {
          //Hvis startpunktet er sat, er det ikke første polylineshapedel, vi er i gang med at tegne.
          var plShape = PolylineShape(points :+ v)
          def setAttribute[T : Manifest](name:String, shape:Shape) = {
            Siigna.get(name) match {
              case s : Some[T] => shape.addAttribute(name, s.get)
              case None => shape// Option isn't set. Do nothing
            }
          }
        }
      }

      case End :: tail => {
        var plShape = PolylineShape(points)
        def setAttribute[T : Manifest](name:String, shape:Shape) = {
          Siigna.get(name) match {
            case s : Some[T] => shape.addAttribute(name, s.get)
            case None => shape// Option isn't set. Do nothing
          }
        }
        val polyLine = setAttribute[Color]("Color",
          setAttribute[Double]("LineWeight", plShape)
        )
      Create(polyLine)
      startPoint = None
      points = List()
      End
      }
      case _ => {
        println("Starter polyline-modul, ikke med END")
        Start('Point,"com.siigna.module.base.create")
      }
    })

}

  /**
  def stateMachine = Map(
  'StartCategory -> ((events : List[Event]) => {
    //Log.level += Log.DEBUG + Log.SUCCESS
    events match {
      case MouseDown(_, MouseButtonRight, _) :: tail => {
        Goto('End)
      }
      case _ => Module('Point)
    }
  }),
  'SetPoint -> ((events : List[Event]) => {

    set("activeLineWeight", "StrokeWidth")
    set("activeColor", "Color")

    def getPointGuide = (p : Vector2D) => {
      if(!points.isEmpty) {
        set("activeLineWeight", "StrokeWidth")
        set("activeColor", "Color")
        PolylineShape(points :+ p).setAttributes(attributes)
    }
      else PolylineShape(Vector2D(0,0),Vector2D(0,0))
    }
    events match {
      // Exit strategy
      case (MouseDown(_, MouseButtonRight, _) | MouseUp(_, MouseButtonRight, _) | KeyDown(Key.Esc, _)) :: tail => Goto('End, false)

      case Message(p : Vector2D) :: tail => {
        // Save the point
        points = points :+ p
        // Define shape if there is enough points
        if (points.size > 1) {
          shape = Some(PolylineShape(points))
        }
        Module('Point)
        Controller ! Message(PointGuide(getPointGuide))
      }

      // Match on everything else
      case _ => {
        Module('Point)
        Controller ! Message(PointGuide(getPointGuide))
      }
    }
  }),
  'End -> ((events : List[Event]) => {
    if(shape.isDefined) CreateCategory(shape.get.setAttributes(attributes))

    //clear the vars
    com.siigna.module.base.ModuleInit.previousModule = Some('Polyline)
    shape = None
    points = List()
  }))
}*/