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

/* 2012 (C) Copyright by Siigna, all rights reserved. */

import com.siigna._
import app.Siigna
import java.awt.Color

class Arc extends Module {

  var r = TransformationMatrix()

  var startPoint : Option[Vector2D] = None
  var endPoint : Option[Vector2D] = None

  def stateMap = Map(
    //StartCategory: Defines a start point for the arc and forwards to 'SetEndPoint
    'Start -> {
      case events => {
        events match {
          case MouseDown(_, MouseButtonRight, _) :: tail => 'End
          case End(p : Vector2D) :: tail => {
            //If no start point is set, the recieved point becomes the start point,
            //and a line guide is returned (between the start and end point)
            if (startPoint.isEmpty) {
              startPoint = Some(p)
              Start('Point, "com.siigna.module.base.create",
                Guide(v => Traversable(LineShape(p,v)))
              )
            //If the end point is not set, and the point recieved is not the same as the start point,
            //the recieved point is set as the end point, and an arc guide is returned.
            } else if ((endPoint.isEmpty) && (startPoint.get != p)) {
              endPoint = Some(p)
              Start('Point, "com.siigna.module.base.create",
                Guide(v => Traversable(ArcShape(startPoint.get,v,endPoint.get)))
              )
            //If the end point is set, but the recieved point is the same as the start point,
            //the recieved point is ignored, and a line guide (between point 1 and 2) is returned again.
            } else if ((endPoint.isEmpty) && (startPoint.get == p)){
              Start('Point, "com.siigna.module.base.create",
                Guide(v => Traversable(LineShape(p,v)))
              )
            //If neither start or endpoint is empty, and:
            // the recieved point is not the same as start or endpoint, and
            // the three points are not on a straight line, then
            // an arc shape is created and module closes.
            } else if ((startPoint.get != p) && (endPoint.get != p) && (math.abs((startPoint.get.x - p.x)/(startPoint.get.y - p.y) ) != math.abs((endPoint.get.x - p.x)/(endPoint.get.y - p.y)))) {
                val arc = ArcShape(startPoint.get,p,endPoint.get)
                def setAttribute[T : Manifest](name:String, shape:Shape) = {
                  Siigna.get(name) match {
                    case s : Some[T] => shape.addAttribute(name, s.get)
                    case None => shape// Option isn't set. Do nothing
                  }
                }
                Create(setAttribute[Color]("Color",
                  setAttribute[Double]("LineWeight", arc)
                ))
                End
            } else {
            //If start and endpoint is set, but the third point is the same as the start or end point,
            //or the three points are inline, the recieved point is unusable, and
            //the recieved point is ignored and an arc guide returned again.
              println ("The three points are in-line, or the third point is the same as one of the two first in arc module.")
              Start('Point, "com.siigna.module.base.create",
                Guide(v => Traversable(ArcShape(startPoint.get,v,endPoint.get)))
              )
            }
          }
          //If an end command is recieved without a new point, the module exits
          case End :: tail => End
          //If an unknown command is recieved (could happen if guide fails)
          case _ => {
            println("Unknown command in Arc-module: ")
            //If user is drawing something, the guides should still be drawn:
            if ((startPoint.isDefined) && (endPoint.isEmpty)) {
              Start('Point, "com.siigna.module.base.create",
                Guide(v => Traversable(LineShape(startPoint.get,v)))
              )
            } else if ((startPoint.isDefined) && (endPoint.isDefined)) {
              Start('Point, "com.siigna.module.base.create",
                Guide(v => Traversable(ArcShape(startPoint.get,v,endPoint.get)))
              )
            } else {
            Start('Point, "com.siigna.module.base.create")
            }
          }
        }
      }
    }
  )

}
