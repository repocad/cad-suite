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

package com.siigna.module.base

import com.siigna._

object Selection extends Module {

  lazy val eventHandler = EventHandler(stateMap, stateMachine)

  private var box : Option[Rectangle2D] = None

  // The starting point of the rectangle
  private var startPoint : Option[Vector2D] = None

  /**
   * Examines whether the selection is currently enclosed or not.
   */
  def isEnclosed : Boolean = startPoint.isDefined && startPoint.get.x <= Siigna.mousePosition.x

  def stateMap     = DirectedGraph(
    'Start -> 'MouseDrag   -> 'Box,
    'Start -> 'MouseMove   -> 'End,
    'Start -> 'MouseUp     -> 'End,
    'Box   -> 'MouseMove   -> 'End,
    'Box   -> 'MouseUp     -> 'End
  )

  def stateMachine = Map(
    'Start -> ((events : List[Event]) => {
      events match {
        case MouseDown(p, _, _) :: tail => startPoint = Some(p)
        case _ => Goto('End)
      }
    }),
    'Box -> ((events : List[Event]) => {
      events match {
        case MouseDrag(p, _, _) :: tail => {
          box = Some(Rectangle2D(startPoint.get, p))
        }
        case _ => Goto('End)
      }
    }),
    'End -> ((events : List[Event]) => {
      if (box.isDefined) {
        Select(Model(box.get))
        box = None
      }
    })
  )

  override def paint(g : Graphics, t : TransformationMatrix) {
    val enclosed = "Color" -> "#9999FF".color
    val focused  = "Color" -> "#FF9999".color
    if (box.isDefined) {
        g draw PolylineShape.fromRectangle(box.get).addAttribute("Color" -> (if (isEnclosed) enclosed else focused)).transform(t)
    }
  }
}