package org.hyperscala

import org.jdom2.{Element, Content}
import persistence.StringPersistence
import org.powerscala.property.PropertyParent

/**
 * @author Matt Hicks <mhicks@powerscala.org>
 */
trait Textual extends Markup with PropertyParent {
  implicit val contentPersistence = StringPersistence

  val content = PropertyAttribute[String]("content", null, exclude = true)

  override def toXML: Content = {
    val element = super.toXML.asInstanceOf[Element]
    element.setText(content())
    element
  }

  override def fromXML(xml: Content) {
    super.fromXML(xml)
    val element = xml.asInstanceOf[Element]
    content := element.getText.trim
  }
}
