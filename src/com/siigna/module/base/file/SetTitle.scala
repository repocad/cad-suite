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

package com.siigna.module.base.file

import com.siigna.app.controller.remote.Set
import com.siigna._
import module.base.Default

object SetTitle extends Module {

  var boundary = Model.boundary

  val eventHandler = EventHandler(stateMap, stateMachine)

  var text         = ""

  def stateMap = DirectedGraph (
    'Start    -> 'Event     -> 'End
  )

  def stateMachine = Map(

    'Start -> ((events : List[Event]) => {
      //TODO: A CLICK IS NEEDED BEFORE SIIGNA REGISTERES KEY INPUT. TO BE FIXED.
      View.zoom(Default.titleFocus.get, -2)
      Siigna display "type a drawing title"
      Goto('TextInput)
    }),
    'TextInput -> ((events : List[Event]) => {
      events match {
        case KeyDown(Key.Backspace, _) :: tail => {
            if (text.length != 0) {
              text = text.substring(0, text.length - 1)
              Siigna display text
            }
            else Goto('End)
        }
        case KeyDown(Key.Enter, _) :: tail => Goto('End)
        case KeyDown(Key.Esc, _) :: tail => {
          text = ""
          Goto('End)
        }
        case KeyDown(key, _) :: tail => {
          text += key.toChar.toString.toLowerCase        }
          Siigna display text

        case MouseUp(_, MouseButtonRight, _) :: tail => Goto('End)
        case _ =>
      }
      None
    }),
    'End -> ((events : List[Event]) => {
      //save a new name in the databasen (automatically corrects the applet's variable)
      if (Siigna.client.isDefined) {
        Set("drawingName", text, Siigna.client.get)

        //reset the vars
        text = ""
      }
    })
  )
}