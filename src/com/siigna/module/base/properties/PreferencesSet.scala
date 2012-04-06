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

package com.siigna.module.base.properties


import com.siigna._

object PreferencesSet extends Module {

  def eventHandler = EventHandler(stateMap, stateMachine)

  def stateMap     = DirectedGraph(
    'Start -> 'MouseDown -> 'End
  )

  lazy val stateMachine = Map(
    'Start -> ((events : List[Event]) => {
      events match {
        case _ =>
      }
    }),
    'SelectionDistance -> ((events : List[Event]) => {
      events match {
        case _ => println("set selection distance here")
      }
    }),
    'End -> ((events : List[Event]) => {
      events match {
        case _ => println("ending prefs module")
      }
    })
  )
}