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
import java.nio.file.OpenOption

/**
 * The point module answers to requests from many other modules who require a number or one or more points to function.
 *
 */

class Input extends Module {

  //VARS declaration:
  private var decimalValue : Boolean = false
  private var point1 : Option[Vector2D] = None
  private var point2 : Option[Vector2D] = None
  private var guide : Boolean = true
  private var inputType : Option[Int] = None
  var pointGuide : Option[Vector2D => Traversable[Shape]] = None
  var pointPointGuide : Option[Vector2D => Traversable[Shape]] = None
  var pointDoubleGuide : Option[Double => Traversable[Shape]] = None
  var pointPointDoubleGuide : Option[Double => Traversable[Shape]] = None
  var pointPointPointGuide : Option[Vector2D => Traversable[Shape]] = None
  var sendPointGuide : Option[PointGuide] = None
  var sendDoubleGuide : Option[DoubleGuide] = None
  var sendPointPointGuide : Option[PointPointGuide] = None
  var sendPointDoubleGuide : Option[PointDoubleGuide] = None
  var sendPointPointDoubleGuide : Option[PointPointDoubleGuide] = None
  var sendPointPointPointGuide : Option[PointPointPointGuide] = None
  var snapAngle : Option[Double] = None
  val stateMap: StateMap = Map(

    'Start -> {
      //if InputTwoValue returns a vector, return it to the calling module:
      case End(p : Vector2D) :: tail => {
        if (inputType == Some(1) || inputType == Some(2)) {
          End(p)
        } else if (inputType == Some(102)) {
          End(MouseDown(p,MouseButtonLeft,ModifierKeys(false,false,false)))
        }
      }
      //if a single value is returned from InputOneValue of InputAngle, return it to the calling module:
      case End(s : Double) :: tail => {
        End(s)
      }

      //If an end command is recieved without input (from an input module):
      case End("no point returned") :: tail => {
        End("no point returned")
      }

      //If left mouse button is clicked: End and return mouse-position-point.
      case MouseDown(p,button,modifier)::tail => {
        if (button==MouseButtonLeft) {
          if (inputType == Some(1)) {
            End(p.transform(View.deviceTransformation))
          } else if (inputType == Some(2) || inputType == Some(4) || inputType == Some(6) | inputType == Some(8))  {
            point1 = Some(p)
            //Start painting, if it has been turned off (point guide)
            if (guide == false) guide = true
          } else if (inputType == Some(3))  {
            //Type is distance from start point, returned as double
            val startPointX = point1.get.x
            val startPointY = point1.get.y
            val distanceFromStartToMouse: Double = math.sqrt(( (startPointX-mousePosition.transform(View.deviceTransformation).x) * (startPointX-mousePosition.transform(View.deviceTransformation).x)) + ( (startPointY-mousePosition.transform(View.deviceTransformation).y) * (startPointY-mousePosition.transform(View.deviceTransformation).y)) )
            if (distanceFromStartToMouse != 0) {
              End(distanceFromStartToMouse)
            }
          } else if (inputType == Some(5))  {
              End(p.transform(View.deviceTransformation).x)
          } else if (inputType == Some(7)) {
              End(p.transform(View.deviceTransformation).y)
          } 
        } else {
          // In all other cases, where it is not left mouse button, the mouseDown is returned
          End(MouseDown(p.transform(View.deviceTransformation),button,modifier))
        }
      }

      //If mouse up is recieved,
      // 1: If input type is three the mouse up is returned.
      // 2: If input type is 4, the difference between point and the start point is returned.
      case MouseUp(p,button,modifier)::tail => {
        if (inputType.get == 2) {
          End(Vector2D((p - point1.get).x,-(p - point1.get).y))
        } else if (inputType.get == 4) {
          //Type is distance from start point, returned as double
          val startPointX = point1.get.x
          val startPointY = point1.get.y
          val distanceFromStart: Double = math.sqrt(( (startPointX-p.transform(View.deviceTransformation).x) * (startPointX-p.transform(View.deviceTransformation).x)) + ( (startPointY-p.transform(View.deviceTransformation).y) * (startPointY-p.transform(View.deviceTransformation).y)) )
          if (distanceFromStart != 0) {
            End(distanceFromStart)
          }
        } else if (inputType.get == 6) {
          End(p.x - point1.get.x)
        } else if (inputType.get == 8) {
          End(p.y - point1.get.y)
        } else if (inputType.get == 8) {
          End(MouseUp(Vector2D((p - point1.get).x,-(p - point1.get).y),MouseButtonLeft,ModifierKeys(false,false,false)))
        } else if (inputType.get == 9) {
        End(p.transform(View.deviceTransformation))
      }

      }

      // Check for PointGuide - retrieve only the guide, no reference point. 
      //Returns coordinate difference from mouseDown to Mouse UP, or key-entries.
      case Start(_ ,g : PointGuide) :: tail => {
        pointGuide = Some(g.pointGuide)
        inputType = Some(g.inputType)
        sendPointGuide = Some(g)
        //Paint nothing, yet - not until the first click...
        guide = false
      }

      // Check for PointPointGuide - retrieve both the guide and its reference point, if it is defined.
      case Start(_ ,g : PointPointGuide) :: tail => {
        pointPointGuide = Some(g.pointGuide)
        inputType = Some(g.inputType)
        sendPointPointGuide = Some(g)
        point1 = Some(g.point1)
      }

      // Check for PointDoubleGuide - retrieve both the guide and its reference point, if it is defined.
      case Start(_ ,g : PointDoubleGuide) :: tail => {
        pointDoubleGuide = Some(g.doubleGuide)
        inputType = Some(g.inputType)
        sendPointDoubleGuide = Some(g)
        point1 = Some(g.point1)
      }

      // Check for PointPointDoubleGuide - retrieve both the guide and its reference point, if it is defined.
      case Start(_ ,g : PointPointDoubleGuide) :: tail => {
        pointPointDoubleGuide = Some(g.doubleGuide)
        inputType = Some(g.inputType)
        sendPointPointDoubleGuide = Some(g)
        point1 = Some(g.point1)
        point2 = Some(g.point2)
      }

      // Check for PointPointPointGuide - retrieve both the guide and its reference point, if it is defined.
      case Start(_ ,g : PointPointPointGuide) :: tail => {
        pointPointPointGuide = Some(g.pointGuide)
        inputType = Some(g.inputType)
        sendPointPointPointGuide = Some(g)
        point1 = Some(g.point1)
        point2 = Some(g.point2)
      }

      //If there is no guide, only the input type needs to be retrieved
      case Start(_,inp: Int) :: tail => {
        inputType = Some(inp)
      }

      // Exit strategy
      case KeyDown(Key.Esc, _) :: tail => End

      //TODO: add if statement: if a track-guide is active, forward to a InputLength module instead...


      case KeyDown(key,modifier) :: tail => {

        //If the input is backspace with no modifiers, this key is returned to the asking module:
        if (key == Key.backspace && modifier == ModifierKeys(false,false,false)) {
          (End(KeyDown(key,modifier)))
          //If it is other keys, the input is interpreted by the input-modules.
          //Any existing guides are forwarded.
        } else if(inputType == Some(1) || inputType == Some(2) || inputType == Some(102)) {
          if (guide == true) guide = false
          if (!sendPointGuide.isEmpty) Start('InputTwoValues,"com.siigna.module.base.create",sendPointGuide.get)
          else if (!sendDoubleGuide.isEmpty) Start('InputTwoValues,"com.siigna.module.base.create", sendDoubleGuide.get)
          else if (!sendPointPointGuide.isEmpty) Start('InputTwoValues,"com.siigna.module.base.create",sendPointPointGuide.get)
          else if (!sendPointDoubleGuide.isEmpty) Start('InputTwoValues,"com.siigna.module.base.create", sendPointDoubleGuide.get)
          else if (!sendPointPointDoubleGuide.isEmpty) Start('InputTwoValues,"com.siigna.module.base.create", sendPointPointDoubleGuide.get)
          else if (!sendPointPointPointGuide.isEmpty) Start('InputTwoValues,"com.siigna.module.base.create", sendPointPointPointGuide.get)
          else Start('InputTwoValues,"com.siigna.module.base.create")
        } else if(inputType == Some(3) || inputType == Some(4) || inputType == Some(5) || inputType == Some(6) || inputType == Some(7) || inputType == Some(8)) {
          if (guide == true) guide = false
          if (!sendPointPointGuide.isEmpty) Start('InputOneValue,"com.siigna.module.base.create", sendPointPointGuide.get)
          else if (!sendPointDoubleGuide.isEmpty) Start('InputOneValue,"com.siigna.module.base.create", sendPointDoubleGuide.get)
          else Start('InputOneValue,"com.siigna.module.base.create")
        }

      }
      case _ => {
      }
    }
  )
  override def paint(g : Graphics, t : TransformationMatrix) {
    //draw the guide - but only if no points are being entered with keys, in which case the input modules are drawing.
    if ( guide == true) {
      //If a point is the desired return, x and y-coordinates are used in the guide
      if (!pointGuide.isEmpty) pointGuide.foreach(_(mousePosition.transform(View.deviceTransformation)).foreach(s => g.draw(s.transform(t))))
      //If a point is the desired return, x and y-coordinates are used in the guide
      if (!pointPointGuide.isEmpty) pointPointGuide.foreach(_(mousePosition.transform(View.deviceTransformation)).foreach(s => g.draw(s.transform(t))))
      //If a double is the desired return, the distance from the starting point is used in the guide
      if (!pointDoubleGuide.isEmpty) {
        val startPointX =sendPointDoubleGuide.get.point1.x
        val startPointY =sendPointDoubleGuide.get.point1.y
        val distanceFromStartToMouse: Double = math.sqrt(( (startPointX-mousePosition.transform(View.deviceTransformation).x) * (startPointX-mousePosition.transform(View.deviceTransformation).x)) + ( (startPointY-mousePosition.transform(View.deviceTransformation).y) * (startPointY-mousePosition.transform(View.deviceTransformation).y)) )
        pointDoubleGuide.foreach(_(distanceFromStartToMouse).foreach(s => g.draw(s.transform(t))))
      }
      if (!pointPointPointGuide.isEmpty) {
        pointPointPointGuide.foreach(_(mousePosition.transform(View.deviceTransformation)).foreach(s => g.draw(s.transform(t))))
      }
    }
  }
}

/**
 * inputType (Int) lets the modules tell, what return they accept:
 *     Return:                      Input method:
 * 1 = Vector2D                     MouseDown, Key (handled by the InputTwoValues module)
 * 2 = Vector2D                     Coordinates from mouseDown to mouseUp, or Key (handled by the InputTwoValues module)
 * 3 = Double                       Distance from given start point to point given by mouseDown, or Key (handled by the InputOneValues module)
 * 4 = Double                       Distance from mouse down to mouse up, or Key (handled by the InputOneValues module)
 * 5 = Double                       x-coordinate from mouseDown, or Key
 * 6 = Double                       x-coordinate difference from mouse Down to mouseUp, or key
 * 7 = Double                       y-coordinate from mouseDown, or Key
 * 8 = Double                       y-coordinate difference from mouse Down to mouseUp, or key
 * 9 = Vector2D                     Coordinates at mouseDown)
 *
 * 102 = mouseDown, with Vector2D   MouseDown, Key (handled by the InputTwoValues module)
 *       mouseUp, with Vector2D     Difference from mouseDown to mouseUp
 *
 */



//The basic point guide - a vector2D is the base for the shapes
case class PointGuide(pointGuide : Vector2D => Traversable[Shape] , inputType : Int)

//The basic double guide - a double is the base for the shapes
case class DoubleGuide(pointGuide : Double => Traversable[Shape] , inputType : Int)

//A point and a point guide - a vector2D delivered along a basic point guide,
// for use when the guide needs to relate to a fixed point
case class PointPointGuide(point1 : Vector2D , pointGuide : Vector2D => Traversable[Shape] , inputType : Int)

//A point and a double guide - a vector2D delivered along a basic double guide,
// for use when the guide needs to relate to a fixed point
case class PointDoubleGuide(point1 : Vector2D , doubleGuide : Double => Traversable[Shape] , inputType : Int)

//two points and a point guide - two vector2Ds delivered along a basic point guide,
// for use when the guide needs to relate to two fixed points
case class PointPointDoubleGuide(point1 : Vector2D, point2 : Vector2D, doubleGuide : Double => Traversable[Shape] , inputType : Int)

//two points and a double guide - two vector2Ds delivered along a333 basic double guide,
// for use when the guide needs to relate to two fixed points
case class PointPointPointGuide(point1 : Vector2D, point2 : Vector2D, pointGuide : Vector2D => Traversable[Shape] , inputType : Int)


