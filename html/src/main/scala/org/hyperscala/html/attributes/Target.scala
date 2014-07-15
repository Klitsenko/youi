package org.hyperscala.html.attributes

import org.powerscala.enum.Enumerated
import org.hyperscala.persistence.EnumEntryPersistence
import org.hyperscala.EnumEntryAttributeValue

/**
 * NOTE: This file has been generated. Do not modify directly!
 * @author Matt Hicks <matt@outr.com>
 */
class Target(val value: String) extends EnumEntryAttributeValue

object Target extends Enumerated[Target] with EnumEntryPersistence[Target] {
  val Blank = new Target("_blank")
  val Self = new Target("_self")
  val Parent = new Target("_parent")
  val Top = new Target("_top")

  override def apply(name: String) = get(name) match {
    case Some(t) => t
    case None => new Target(name)
  }
}