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

package com.siigna.module.base.radialmenu.category

import com.siigna.module.base.radialmenu._

case class Geometry(parent : Option[MenuCategory]) extends MenuCategory {

  val color = RadialMenuIcon.createColor

  def name = "Geometry"

  //North
  override def NNE = Some(MenuItem('Circle, RadialMenuIcon.circle ,"create"))
  override def N = Some(MenuItemEmpty("Rounded"))
  override def NNW = Some(MenuItem('Arc, RadialMenuIcon.arc , "create"))

  //EAST
  //override def E = Some(MenuItemEmpty("Euclid"))

  //West
  //override def WNW = Some(MenuItem('ParamTest, RadialMenuIcon.rectangle,"create"))
  //override def W = Some(MenuItemEmpty("Parametric"))
  //override def WSW = Some(MenuItem('Stair, RadialMenuIcon.arc , "create"))
}