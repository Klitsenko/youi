package org.hyperscala.persistence

/**
 * @author Matt Hicks <matt@outr.com>
 */
object DoublePersistence extends ValuePersistence[Double] {
  def fromString(s: String, name: String, clazz: Class[_]) = s.collect {
    case c if (c.isDigit || c == '.') => c
  } match {
    case "" => 0.0
    case v => try {
      v.toDouble
    } catch {
      case t: Throwable => 0.0
    }
  }

  def toString(t: Double, name: String, clazz: Class[_]) = t.toString
}
