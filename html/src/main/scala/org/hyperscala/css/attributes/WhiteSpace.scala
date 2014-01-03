package org.hyperscala.css.attributes

import org.powerscala.enum.{Enumerated, EnumEntry}
import org.hyperscala.persistence.EnumEntryPersistence
import org.hyperscala.AttributeValue

/**
 * NOTE: This file has been generated. Do not modify directly!
 * @author Matt Hicks <matt@outr.com>
 */
sealed class WhiteSpace(val value: String) extends EnumEntry with AttributeValue

object WhiteSpace extends Enumerated[WhiteSpace] with EnumEntryPersistence[WhiteSpace] {
  val Normal = new WhiteSpace("normal")
  val NoWrap = new WhiteSpace("nowrap")
  val Pre = new WhiteSpace("pre")
  val PreWrap = new WhiteSpace("pre-wrap")
  val PreLine = new WhiteSpace("pre-line")
  val Inherit = new WhiteSpace("inherit")
}