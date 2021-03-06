/*
 * Copyright (c) 2008-2013. Siigna is released under the creative common license by-nc-sa. You are free
 * to Share — to copy, distribute and transmit the work,
 * to Remix — to adapt the work
 *
 * Under the following conditions:
 * Attribution —  You must attribute the work to http://siigna.com in the manner specified by the author or licensor (but not in any way that suggests that they endorse you or your use of the work).
 * Noncommercial — You may not use this work for commercial purposes.
 * Share Alike — If you alter, transform, or build upon this work, you may distribute the resulting work only under the same or similar license to this one.
 */

package com.siigna.module.cad.create

import com.siigna._
import com.siigna.app.model.shape.RectangleShape
import module.Tooltip

class Rectangle extends Module {

  val attributes = {
    val color = Siigna.color("activeColor")
    val lineWidth = Siigna.double("activeLineWidth")
    Attributes(Seq(color.map(c => "Color" -> color.getOrElse(None)), lineWidth.map(w => "StrokeWidth" -> lineWidth.getOrElse(None))).flatten)
  }

  var points = List[Vector2D]()
  val stateMap: StateMap = Map(

    'Start -> {
      //exit mechanisms
      case End(MouseDown(p,MouseButtonRight,modifier)) :: tail => End
      case End(KeyDown(Key.escape,modifier)) :: tail => End
      case MouseDown(p,MouseButtonRight,modifier) :: tail => End
      case KeyDown(Key.escape,modifier) :: tail => End

      case End(v : Vector2D) :: tail => {
        //use the first point
        if (points.length == 0){
          points = points :+ v
          val vector2DGuide = DynamicDrawFromVector2D((v: Vector2D) => Traversable(PolylineShape(Rectangle2D(points(0), v)).addAttributes(attributes)))
          val inputRequest = InputRequest(7,Some(v),vector2DGuide)
          Start('cad, "create.Input", inputRequest)
        }
        //use second point
        else if (points.length == 1) {
          points = points :+ v
          //create the rectangle
          Create(RectangleShape(points(0),points(1)).addAttributes(attributes))
          //Create(PolylineShape(Rectangle2D(points(0), points(1))).addAttributes(attributes))
          points = List()
          End
        }
      }

      //If End with no point: End module without drawing anything.
      case End :: tail => End
      //get the first point
      case _ => {
        //change cursor to crosshair
        Siigna.setCursor(Cursors.crosshair)

        if (points.length == 0) {
          Tooltip.updateTooltip(List("Set corner points"))
          Start('cad, "create.Input", InputRequest(6,None))
        } else {
          val vector2DGuide = DynamicDrawFromVector2D((v: Vector2D) => Traversable(PolylineShape(Rectangle2D(points(0), v)).addAttributes(attributes)))
          val inputRequest = InputRequest(7,Some(points.head),vector2DGuide)
          Start('cad, "create.Input", inputRequest)
        }
      }
    }
  )
}