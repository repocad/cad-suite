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

package com.siigna.module.cad.create

import com.siigna._
import app.Siigna

/**
 * A module that collect a pair of digits on the basis of key inputs.
 * Used by modules that need eg. an X and Y coordinate to define a point.
 */

class InputTextByKey extends Module {

  private var text : String = ""  //input string

  //Information received from calling module
  var inputRequest: Option[InputRequest] = None
  var inputType: Option[Int] = None
  var guides: Seq[Guide] = Seq()
  var referencePoint: Option[Vector2D] = None


  val stateMap: StateMap = Map(

    'Start -> {
      //exit mechanisms
      case MouseDown(p,MouseButtonRight,modifier) :: tail => End
      case KeyDown(Key.escape,modifier) :: tail => End

      case Start(_ ,i: InputRequest) :: KeyDown(code, _) :: tail => {
        inputRequest = Some(i)
        inputType = Some(i.inputType)
        guides = i.guides
        referencePoint = i.referencePoint
        //save the already typed key:
        if (code.toChar.isValidChar) text += code.toChar
      }

      //Read numbers and minus, "," and enter as first entry if no guide is provided:
      case Start(_,_) :: KeyDown(code, _) :: tail => {
        //save the already typed key:
        if (code.toChar.isValidChar) text += code.toChar
      }

      //Ends on return, komma, TAB - returning value:
      case KeyDown(Key.Enter | Key.Tab | (','), _) :: tail => {
        if (text.length > 0) {
          End(text)
        }
      }

      case KeyDown(Key.Backspace, _) :: tail => {
        if (text.length > 0) text = text.substring(0, text.length-1)
        //The string must be at least a space - an empty string makes the message-function puke...
        //The string must be at least a space - an empty string makes the message-function puke...
        if (text.length == 0) text += " "
      }

      //if point returns a keyDown - that is not previously intercepted
      case KeyDown(code, modifier) :: tail => {
        //get the input from the keyboard if it is numbers, (-) or (.)
        val char = code.toChar
        if (char.isValidChar)
          text += char

      }
      case _ => {
        if (text.length == 0) End
      }
    })

  override def paint(g : Graphics, t: TransformationMatrix) {
    //if text the process of being typed, then draw the textshape dynamically.
    if (text.length > 0) {
    guides.foreach(_ match {

      case TextGuide(guide) => {
        guide(text).foreach(s => g.draw(s.transform(t)))
      }
      case _ => // No known guide
    } )
  } }
}

