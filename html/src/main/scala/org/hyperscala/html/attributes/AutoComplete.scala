package org.hyperscala.html.attributes

import org.powerscala.enum.{Enumerated, EnumEntry}
import org.hyperscala.persistence.EnumEntryPersistence
import org.hyperscala.AttributeValue

/**
 * NOTE: This file has been generated. Do not modify directly!
 * @author Matt Hicks <matt@outr.com>
 */
sealed class AutoComplete(val value: String) extends EnumEntry with AttributeValue

object AutoComplete extends Enumerated[AutoComplete] with EnumEntryPersistence[AutoComplete] {
  val On = new AutoComplete("on")
  val Off = new AutoComplete("off")
}