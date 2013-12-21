package org.hyperscala.ui.dynamic

import org.powerscala.IO
import java.io.File
import java.net.URL

/**
 * @author Matt Hicks <mhicks@outr.com>
 */
trait DynamicString {
  def name = DynamicString.name(this)
  def lastModified: Long
  def content: String
}

class DependentDynamicString protected[dynamic](dynamicString: DynamicString, val converter: String => String) extends DynamicString {
  private var originalContent: String = _
  private var converted: String = _

  def lastModified = dynamicString.lastModified
  def content = {
    checkUpdate()
    converted
  }

  protected def checkUpdate() = {
    val newContent = dynamicString.content
    if (newContent != originalContent) {
      converted = converter(newContent)
      originalContent = newContent
      refresh()
    }
  }

  protected def refresh() = {
  }
}

class GeneralDynamicString protected[dynamic](val contentFunction: () => String, val lastModifiedFunction: () => Long, val converter: String => String) extends DynamicString {
  private var _lastModified: Long = _
  private var _content: String = _

  refresh(force = true)

  def lastModified = {
    refresh()
    _lastModified
  }
  def content = {
    refresh()
    _content
  }

  def set(content: String, lastModified: Long = System.currentTimeMillis()) = synchronized {
    _content = content
    _lastModified = lastModified
    modified(content)
  }

  def refresh(force: Boolean = false): Unit = {
    val modified = lastModifiedFunction()
    if (force || modified > _lastModified) {
      val content = contentFunction()
      val converted = converter(content)
      set(converted, modified)
    }
  }

  protected def modified(content: String) = {}
}

object DynamicString {
  private var map = Map.empty[String, DynamicString]

  private def name(dynamicString: DynamicString) = map.collectFirst {
    case (name, ds) if (ds == dynamicString) => name
  }.getOrElse(throw new NullPointerException("Unable to find name for %s.".format(dynamicString)))

  def getOrSet[T <: DynamicString](name: String, creator: => T): T = synchronized {
    map.get(name) match {
      case Some(ds) => ds.asInstanceOf[T]
      case None => {
        val ds: T = creator
        map += name -> ds.asInstanceOf[DynamicString]
        ds
      }
    }
  }

  val defaultConverter = (s: String) => s

  def dependent(name: String, dynamicString: DynamicString, converter: String => String = defaultConverter) = {
    getOrSet[DynamicString](name, new DependentDynamicString(dynamicString, converter))
  }
  def static(name: String, content: String, converter: String => String = defaultConverter) = {
    getOrSet[DynamicString](name, new GeneralDynamicString(contentFunction(content), defaultLastModifyFunction, converter))
  }
  def dynamic(name: String, content: String, converter: String => String = defaultConverter) = {
    getOrSet[DynamicString](name, new GeneralDynamicString(contentFunction(content), () => System.currentTimeMillis(), converter))
  }
  def file(name: String, file: File, converter: String => String = defaultConverter) = {
    getOrSet[DynamicString](name, new GeneralDynamicString(contentFunction(file), lastModifyFunction(file), converter))
  }
  def url(name: String, url: URL, checkLastModified: Boolean = false, converter: String => String = defaultConverter) = {
    getOrSet[DynamicString](name, new GeneralDynamicString(contentFunction(url), lastModifyFunction(url, checkLastModified), converter))
  }

  def contentFunction(content: String) = () => content
  def contentFunction(file: File) = () => IO.copy(file)
  def contentFunction(url: URL) = () => IO.copy(url)

  val defaultLastModifyFunction = {
    val initial = System.currentTimeMillis()
    () => initial
  }
  def lastModifyFunction(file: File) = () => file.lastModified()
  def lastModifyFunction(url: URL, checkLastModified: Boolean = false) = if (checkLastModified) {
    () => IO.lastModified(url)
  } else {
    defaultLastModifyFunction
  }
}