package org.hyperscala.persistence

/**
 * @author Matt Hicks <matt@outr.com>
 */
object IntPersistence extends ValuePersistence[Int] {
  def fromString(s: String, name: String, clazz: Class[_]) = s.collect {
    case c if c.isDigit | c == '-' => c
  } match {
    case "" => 0
    case v => v.toInt
  }

  def toString(t: Int, name: String, clazz: Class[_]) = t.toString
}
