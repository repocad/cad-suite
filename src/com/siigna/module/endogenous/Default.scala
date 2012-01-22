/* 2010 (C) Copyright by Siigna, all rights reserved. */

package com.siigna.module.endogenous

import com.siigna._
import com.siigna.module.endogenous.radialmenu._
import category.{Modify, MenuCategory}

/**
* The default module for the endogenous module pack. Works as access point to
* the rest of the modules.
*/
object Default extends Module {

  def eventHandler = EventHandler(stateMap, stateMachine)

  def stateMap     = DirectedGraph( 'Start -> 'Event -> 'Start )

  def fullScreenBox = Rectangle2D(Siigna.screen.bottomRight - Vector2D(20, -5), Siigna.screen.bottomRight - Vector2D(40, -25))

  var firstStart : Boolean = true
  var firstMenuLoad : Boolean = true

   //The nearest shape to the current mouse position.
  var nearestShape : Option[Shape] = None

  private var message : String = ""

  //The last module this module forwarded to, if any.
  var previousModule : Option[Symbol] = None

  //store the latest Key Event to be able to see whether a category (C,H,E,P, or F) was chosen before
  var previousKey :Option[Char] = None

  def stateMachine = Map( 'Start -> ((events : List[Event]) => {
      nearestShape = Model(Siigna.mousePosition)
      if (firstStart == true) {
        interface.display("Siigna modules ver. 0.1.11.6 (421 kb")
        firstStart = false
      }
      events match {
        case MouseDown(point, MouseButtonLeft, _) :: tail           => {
          if (fullScreenBox.contains(point.transform(Siigna.physical))) {

          } else ForwardTo('Select)
        }
        case MouseDown(point, MouseButtonRight, _) :: tail          => {
          if (firstMenuLoad == true) {
            interface.display("...loading modules, please wait")
            Thread.sleep(500)
            interface.clearDisplay()

            ForwardTo('Menu)

            //preload commonly used modules
            Preload('Polyline)
            Preload('Artline)
            Preload('Text)
            Preload('Rectangle)
            Preload('Lineardim)
            firstMenuLoad = false
          }
          else ForwardTo('Menu)
        }

        //KEY INPUTS
        case KeyDown(('z' | 'Z'), ModifierKeys(_, true, _)) :: tail => Model.undo
        case KeyDown(('y' | 'Y'), ModifierKeys(_, true, _)) :: tail => Model.redo
        case KeyDown('a', ModifierKeys(_, true, _)) :: tail         => Model.selectAll
        case KeyDown(key, _) :: tail => {
          key.toChar match {
            case Key.Backspace | Key.Delete => {
              if (Model.isSelected) {
                val selected = Model.selected
                Model.deselect
                Delete(selected)
              }
            }
            case Key.Control => {
              if (Model.isSelected) ForwardTo('Copy)
            }
            case Key.Escape => {
              Model.deselect
            }
            case Key.Space => {
              if (previousModule.isDefined) ForwardTo(previousModule.get)
            }
            case 'a' => {
              interface.display("draw artline")
              Thread.sleep(300)
              interface.clearDisplay()
              previousModule = Some('Artline)
              previousKey = Some('a')
              ForwardTo('Artline)
            }
            //TODO: fix circle shortcut (circle does not draw)
            case 'c' => {
              if(previousKey == 'c') {
                interface.display("draw circle")
                Thread.sleep(300)
                interface.clearDisplay()
                ForwardTo('Circle)
              }
              //open the CREATE menu
              else {
                message = "Create"
                Send(Message(message))
                ForwardTo('Menu)
                previousKey = Some('c')
              }
            }
            case 'd' => {
              interface.display("click to create linear dimension line")
              Thread.sleep(300)
              interface.clearDisplay()
              previousKey = Some('d')
              ForwardTo('Lineardim)
            }
            //open the FILE menu
            case 'f' => {
                message = "File"
                Send(Message(message))
                ForwardTo('Menu)
                previousKey = Some('f')
            }
            //open the HELPERS menu
            case 'h' => {
                message = "Helpers"
                Send(Message(message))
                ForwardTo('Menu)
                previousKey = Some('f')
            }
            //open the MODIFY menu
            case 'm' => {
                message = "Modify"
                Send(Message(message))
                ForwardTo('Menu)
                previousKey = Some('m')
            }
            case 'l' => {
              println(previousKey)
              if (previousKey == 'c') {
                interface.display("draw polyline")
                Thread.sleep(300)
                interface.clearDisplay()
                ForwardTo('Polyline)
              }
              else previousKey = Some('l')
            }
            //TODO: fix opening print dialog with shortcut - opens again & again (last event (p) continously evoked??)
            //case 'p' => {
            //  interface.display("opening print dialog")
            //  Thread.sleep(500)
            //  interface.clearDisplay()
            //  ForwardTo('Print)
            //}
            case 'r' => {
              //interface.display("click to draw rectangle")
              //Thread.sleep(500)
              //interface.clearDisplay()
              previousKey = Some('r')
              ForwardTo('Rectangle)
            }
            //open the PROPERTIES menu
            case 'p' => {
                message = "Properties"
                Send(Message(message))
                ForwardTo('Menu)
                previousKey = Some('p')
            }
            case 't' => {
              interface.display("click (twice??) to place text")
              Thread.sleep(500)
              interface.clearDisplay()
              previousKey = Some('t')
              ForwardTo('Text)
            }
            case _ =>
          }
        }
        case m => Log.debug("Default module received unknown input: " + m)
      }
    }))

  override def paint(g : Graphics, t : TransformationMatrix) {
    if (nearestShape.isDefined) {
      g draw nearestShape.get.addAttribute("Color" -> "#AAAAFF".color).transform(t)
    }

    // Draw boundary
    drawBoundary(g, t)
  }

  private def drawBoundary(g : Graphics, t : TransformationMatrix) {
    def unitX(times : Int) = Vector2D(times * Siigna.paperScale, 0)

    // Get the boundary
    val boundary = Model.boundary

    // Define header
    val headerHeight = scala.math.min(boundary.height, boundary.width) * 0.025
    // Paper scale
    val scale = TextShape("Scale 1:"+Siigna.paperScale, unitX(1), headerHeight * 0.7)
    // Get URL
    val getURL = TextShape(" ", Vector2D(0, 0), headerHeight * 0.7)

    val headerWidth  = (scale.boundary.width + getURL.boundary.width) * 1.2
    val headerBoundary = Rectangle2D(boundary.bottomRight, boundary.bottomRight - Vector2D(headerWidth, -headerHeight))

    val transformation : TransformationMatrix = t.concatenate(TransformationMatrix(boundary.bottomRight - Vector2D(headerWidth * 0.99, -headerHeight * 0.8), 1))

    // Draw header
    g draw LineShape(headerBoundary.topLeft, headerBoundary.topRight)
    g draw LineShape(headerBoundary.topRight, headerBoundary.bottomRight)
    g draw LineShape(headerBoundary.bottomRight, headerBoundary.bottomLeft)
    g draw LineShape(headerBoundary.bottomLeft, headerBoundary.topLeft)
    //g draw separator
    g.draw(scale.transform(transformation))
    g.draw(getURL.transform(transformation.translate(scale.boundary.topRight + unitX(4))))
  }

}