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
import com.siigna.app.view.event.EventSnap

/**
 * Created by IntelliJ IDEA.
 * User: oep
 * Date: 15-01-12
 * Time: 12:23
 * To change this template use File | Settings | File Templates.
 */

class RadianSnap(p: Vector2D, radian : Double) extends EventSnap{

  def parse(event : Event, model : Iterable[ImmutableShape]) = event match {
    case MouseMove(point, a, b) => MouseMove(snapToRadian(point), a, b)
    case some => {
      some
    }
    //transforming events with the snapToRadian function
  }

  //function that takes a point and returns another point
  def snapToRadian(point : Vector2D) : Vector2D = {
    var mouseX = point.x
    var mouseY = point.y
    point
  }


}