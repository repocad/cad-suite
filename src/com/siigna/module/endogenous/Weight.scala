package com.siigna.module.endogenous

import com.siigna.app.view.Graphics
import com.siigna._
import java.awt.Color
import radialmenu.{RadialMenuIcon, MenuEvent, MenuItem}

/**
 * a wheel to select weights (and styles?) for lines
 *
 */

object Weight extends Module {

  var activeAngle : Double = 0

  var activeLine : Double = 0


  //TODO: make this dynamic so that each line is drawn by the same function, only changing the .
  //a list of the possible lines
  lazy val line0 = 0.00
  lazy val line30 = 0.05
  lazy val line60 = 0.09
  lazy val line90 = 0.12
  lazy val line120 = 0.18
  lazy val line150 = 0.20
  lazy val line180 = 0.25
  lazy val line210 = 0.30
  lazy val line240 = 0.40
  lazy val line270 = 0.60
  lazy val line300 = 0.80
  lazy val line330 = 1.00

  var relativeMousePosition : Option[Vector2D] = None
  var startPoint : Option[Vector2D] = None

  //PROPERTIES OF TEXT DESCRIPTIONS
  var boundingRectangle : Option[Rectangle2D] = None
  var text     = ""
  var scale    = 8
  var attributes = Attributes( "TextSize" -> 10)


  def eventHandler = EventHandler(stateMap, stateMachine)

  private var gotMouseDown = false

  def radians(rotation : Int) : List[Int] = {

    var i = 0
    var activeRadians = List[Int]()

    //add all radians to a list
    do {

      activeRadians = i :: activeRadians
      i += rotation

    } while (i <= 360)
    activeRadians
  }

  def stateMap = DirectedGraph(

    'Start  ->   'KeyEscape   ->  'End

  )

  def stateMachine = Map(
    //select a color
    'Start -> ((events : List[Event]) => {
      Siigna.navigation = false // Make sure the rest of the program doesn't move
      eventParser.disable // Disable tracking and snapping
      events match {
        case MouseUp(point, _, _) :: tail => startPoint = Some(Menu.oldCenter)
        case MouseMove(point, _, _) :: tail => relativeMousePosition = Some(point)
        //selects the color to use
        case MouseDown(point, _, _) :: tail => {
          //if the mouse has been pressed once, set the color and go to 'End.
          if (gotMouseDown == true) {
            relativeMousePosition = Some(point)
            //set the color
            if (activeAngle == 0) {activeLine = line0}
            else if (activeAngle == 30) {activeLine = line30}
            else if (activeAngle == 60) {activeLine = line60}
            else if (activeAngle == 90) {activeLine = line90}
            else if (activeAngle == 120) {activeLine = line120}
            else if (activeAngle == 150) {activeLine = line150}
            else if (activeAngle == 180) {activeLine = line180}
            else if (activeAngle == 210) {activeLine = line210}
            else if (activeAngle == 240) {activeLine = line240}
            else if (activeAngle == 270) {activeLine = line270}
            else if (activeAngle == 300) {activeLine = line300}
            else if (activeAngle == 330) {activeLine = line330}
            gotMouseDown = false
            Goto('End)
          }

          else {
            //catch the first mouse down
            startPoint = Some(Menu.oldCenter)
            relativeMousePosition = Some(point)
            gotMouseDown = true
          }
        }
        case _ =>
      }
      //get the current angle from the mouse to the center of the line weight menu
      if (relativeMousePosition.isDefined && startPoint.isDefined) {
        val radian = (relativeMousePosition.get - startPoint.get).angle.toInt
        var calculatedAngle = radian * -1 + 450
        if (calculatedAngle > 360)
          activeAngle = calculatedAngle - 360
        else activeAngle = calculatedAngle
          activeAngle = ((activeAngle +7.5)/30).toInt * 30
      }
    }),
    'End -> ((events : List[Event]) => {
      //clear values and reactivate navigation
      println("ACTIVE LINE: "+activeLine)
      startPoint = None
      relativeMousePosition = None
      eventParser.enable
      Siigna.navigation = true
    })
  )
  override def paint(g : Graphics, transform : TransformationMatrix) = {
    if (startPoint.isDefined && relativeMousePosition.isDefined) {

      //template for lines
      val line = LineShape(Vector2D(47,100), Vector2D(-15,83))

      val sp = startPoint.get.transform(transform)
      val t  = TransformationMatrix(sp,1.3)

      //function to rotate the graphics
      def drawLine (rotation : Int) {

          g draw line.transform(t.rotate(activeAngle-180))
        }

      //TODO: add differentiated lineweight
      //draw the lines
      drawLine(0)
      drawLine(30)
      drawLine(60)
      drawLine(90)
      drawLine(120)
      drawLine(150)
      drawLine(180)
      drawLine(210)
      drawLine(240)
      drawLine(270)
      drawLine(300)
      drawLine(330)

      //draw an outline of the menu
      g draw CircleShape(Vector2D(0,0), Vector2D(0,80)).transform(t)
      g draw CircleShape(Vector2D(0,0), Vector2D(0,118)).transform(t)

      //draw the lines
      radians(30).foreach(radian => { g draw line.transform(t.rotate(radian))})

      //TODO: this is a hack! refactor.
      //draw a text description

      var text30 : TextShape = TextShape(line240.toString+" ", Vector2D(-89.48,66.83), scale, attributes)
      var text60 : TextShape = TextShape(line270.toString+" ", Vector2D(-112.2,16.74), scale, attributes)
      var text90 : TextShape = TextShape(line300.toString+" ", Vector2D(-106.8,-38.01), scale, attributes)
      var text120 : TextShape = TextShape(line330.toString+" ", Vector2D(-74.83,-82.75), scale, attributes)
      var text150 : TextShape = TextShape(line0.toString+" ", Vector2D(-24.74,-105.5), scale, attributes)
      var text180 : TextShape = TextShape(line30.toString+" ", Vector2D(30.01,-100.1), scale, attributes)
      var text210 : TextShape = TextShape(line60.toString+" ", Vector2D(74.75,-68.1), scale, attributes)
      var text240 : TextShape = TextShape(line90.toString+" ", Vector2D(97.48,-18.01), scale, attributes)
      var text270 : TextShape = TextShape(line120.toString+" ", Vector2D(92.12,36.74), scale, attributes)
      var text300 : TextShape = TextShape(line150.toString+" ", Vector2D(60.1,81.48), scale, attributes)
      var text330 : TextShape = TextShape(line180.toString+" ", Vector2D(10.01,104.2), scale, attributes)
      var text0 : TextShape = TextShape(line210.toString+" ", Vector2D(-44.74,98.85), scale, attributes)

      g draw text0.transform(t)
      g draw text30.transform(t)
      g draw text60.transform(t)
      g draw text90.transform(t)
      g draw text120.transform(t)
      g draw text150.transform(t)
      g draw text180.transform(t)
      g draw text210.transform(t)
      g draw text240.transform(t)
      g draw text270.transform(t)
      g draw text300.transform(t)
      g draw text330.transform(t)
    }
  }
}