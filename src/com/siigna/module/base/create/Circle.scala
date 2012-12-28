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

class Circle extends Module {

  val c = CircleShape(Vector2D(0, 0), 50)

  var r = TransformationMatrix()

  var center : Option[Vector2D] = None

  def stateMap = Map(
    //StartCategory: Defines a centerpoint for the circle and forwards to 'SetRadius
    'Start -> {
      case events => {
        events match {
          case End(MouseDown(p,MouseButtonRight,modifier)) :: tail => End
          case End(KeyDown(Key.escape,modifier)) :: tail => End

          //If a point is entered, it is the centre:
          case End(p : Vector2D) :: tail => {
            center = Some(p)
            //Send guides, and ask for one-coordinate input: Radius - from point by click, or by key-entry.
            val doubleGuide = DoubleGuide((r: Double) => Traversable(CircleShape(p, math.abs(r))))
            val vector2DGuide = Vector2DGuide((p: Vector2D) => Traversable(CircleShape(center.get, math.sqrt(( (center.get.x-p.x) * (center.get.x-p.x)) + ( (center.get.y-p.y) * (center.get.y-p.y)) ))))
            val inputRequest = InputRequest(Some(vector2DGuide),Some(doubleGuide),None,None,None,None,center,None,None,Some(3))
            Start('Input, "com.siigna.module.base.create",inputRequest)
          }

          case End(r : Double) :: tail => {
            val circle = CircleShape(center.get, math.abs(r))
            def setAttribute[T : Manifest](name:String, shape:Shape) = {
              Siigna.get(name) match {
                case s : Some[T] => shape.addAttribute(name, s.get)
                case None => shape// Option isn't set. Do nothing
              }
            }
            Create(setAttribute[Color]("Color",
              setAttribute[Double]("LineWeight", circle)
            ))
            End
          }

          case End :: tail => End

          //Starts point, asks for two-value-input (center):
          case _ => {
            Start('Input, "com.siigna.module.base.create",1)
          }
        }
      }
    }
  )

}