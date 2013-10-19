package org.hyperscala.ui.dynamic

import org.hyperscala.html.HTMLTag
import org.jdom2.Element
import org.hyperscala.{Unique, Container}
import org.hyperscala.html.constraints.BodyChild
import org.hyperscala.io.HTMLWriter
import org.jdom2.input.{JDOMParseException, SAXBuilder}
import java.io.{File, StringReader}
import annotation.tailrec
import org.powerscala.property.{CaseClassBinding, Property}
import org.hyperscala.web.Webpage
import org.hyperscala.realtime.Realtime
import java.net.URL
import org.hyperscala.html.tag.Text

/**
 * DynamicContent provides similar functionality to StaticContent rendering pre-defined HTML onto the page in place of
 * this component. However, it adds the ability to load or replace tags by id within the pre-defined HTML content and
 * utilize them at render time.
 *
 * @author Matt Hicks <mhicks@outr.com>
 */
abstract class DynamicContent(existingId: String) extends Container[HTMLTag] with BodyChild with HTMLTag {
  id := existingId
  def dynamicString: DynamicString
  def cached: Boolean = true

  private val dynamicContent = DynamicContent.load(dynamicString, cached)
  private var dynamicBlocks = Map.empty[String, DynamicHTMLBlock]

  /**
   * Loads the tag found within the content by id and returns as T.
   *
   * @param id to find the tag by
   * @param reId sets a unique id to this tag before returning if true (defaults to false)
   * @tparam T the type of HTMLTag being returned
   * @return T
   */
  def load[T <: HTMLTag](id: String, reId: Boolean = false): T = synchronized {
    val dhb = dynamicContent.extract(id)
    val tag = HTMLTag.create(dhb.element.getName)
    tag.read(dhb.element)
    val block = dhb.copy(tag = tag)
    dynamicBlocks += id -> block
    contents += tag

    if (reId) {
      tag.id := Unique()
    }
    tag.asInstanceOf[T]
  }

  /**
   * Loads and then binds the tag (two-way) to the property and hierarchy defined. This allows a convenient way to
   * associate properties with dynamically defined fields.
   *
   * @param id to find the tag by
   * @param property to bind to
   * @param hierarchy name within the property to associate the binding (null represents a direct binding)
   * @param binder provides the bind implementation
   * @tparam T the HTMLTag type
   * @tparam V the value type for association
   * @return T
   */
  def bind[T <: HTMLTag, V](id: String, property: Property[_], hierarchy: String = null)(implicit binder: Binder[T, V]): T = {
    val tag = load[T](id)
    bind[T, V](tag, property, hierarchy)
  }

  /**
   * Sets 'newId' to the id of the tag and then returns the tag. This is useful for single-line chaining.
   *
   * @param tag to modify the id for
   * @param newId the new id to set (defaults to Unique())
   * @tparam T the tag type
   * @return T
   */
  def modifyId[T <: HTMLTag](tag: T, newId: String = Unique()) = {
    tag.id := newId
    tag
  }

  /**
   * Binds the tag (two-way) to the property and hierarchy defined. This allows a convenient way to associate properties
   * with dynamically defined fields.
   *
   * @param tag the tag to bind
   * @param property the property to bind to
   * @param hierarchy the name within the property to associate the binding (null represents a direct binding)
   * @param binder provides the bind implementation
   * @tparam T the HTMLTag type
   * @tparam V the value type for association
   * @return T
   */
  def bind[T <: HTMLTag, V](tag: T, property: Property[_], hierarchy: String)(implicit binder: Binder[T, V]): T = {
    Webpage().require(Realtime)   // Make sure we have realtime access
    binder.bind(tag, property, hierarchy)
    tag
  }

  /**
   * Replaces the tag found within the content by id with the one created by 'f'.
   *
   * @param id to find the tag by and replace with
   * @param f the function to generate the replacement
   * @tparam T the type of HTMLTag being generated to replace the existing content
   * @return T
   */
  def replace[T <: HTMLTag](id: String)(f: => T): T = synchronized {
    val dhb = dynamicContent.extract(id)
    val tag: T = f
    val block = dhb.copy(tag = tag)
    dynamicBlocks += id -> block
    contents += tag

    tag
  }

  /**
   * Removes the tag from use in this instance of DynamicContent.
   *
   * @param id to remove
   */
  def remove(id: String): Unit = synchronized {
    val dhb = dynamicContent.extract(id)
    val t = new Text("")      // Add empty tag
    val block = dhb.copy(tag = t)
    dynamicBlocks += id -> block
    contents += t
  }

  def xmlLabel: String = "dynamiccontent"

  override protected def writeTag(writer: HTMLWriter) = writeBlocks(writer, dynamicContent.blocks)

  @tailrec
  private def writeBlocks(writer: HTMLWriter, blocks: List[HTMLBlock]): Unit = {
    if (blocks.nonEmpty) {
      blocks.head match {
        case shb: StaticHTMLBlock => writer.write(shb.content)
        case dhb: DynamicHTMLBlock => dynamicBlocks.get(dhb.id) match {
          case Some(b) => writer.write(b.content)
          case None => writer.write(dhb.content)
        }
      }
      writeBlocks(writer, blocks.tail)
    }
  }
}

class StringDynamicContent(val dynamicString: DynamicString, existingId: String) extends DynamicContent(existingId)

object DynamicContent {
  private var contents = Map.empty[String, DynamicHTML]

  def apply(dynamicString: DynamicString, existingId: String) = new StringDynamicContent(dynamicString, existingId)

  def url(url: URL, existingId: String, checkLastModified: Boolean = false, converter: String => String = DynamicString.defaultConverter) = {
    new StringDynamicContent(DynamicString.url(url.toString, url, checkLastModified, converter), existingId)
  }

  def file(file: File, existingId: String, converter: String => String = DynamicString.defaultConverter) = {
    new StringDynamicContent(DynamicString.file(file.getAbsolutePath, file, converter), existingId)
  }

  private def load(dynamicString: DynamicString, cache: Boolean = true) = synchronized {
    val content = dynamicString.content
    contents.get(content) match {
      case Some(dhtml) if dhtml == content => dhtml
      case _ => {
        val dhtml = new DynamicHTML(content)
        if (cache) {
          contents += content -> dhtml
        }
        dhtml
      }
    }
  }

  def extract(content: String, id: String) = {
    val idIndex = content.indexOf("id=\"%s\"".format(id))
    if (idIndex == -1) {
      throw new RuntimeException(s"Unable to find $id in DynamicContent")
    }
    val begin = content.lastIndexOf('<', idIndex)
    val end = findCloseIndex(begin, content)
    (content.substring(begin, end), begin, end)
  }

  def extractFunction(id: String, reId: Boolean = false) = (content: String) => {
    val extracted = extract(content, id)._1
    if (reId) {
      extracted.replace("id=\"%s\"".format(id), "id=\"%s\"".format(Unique()))
    } else {
      extracted
    }
  }

  private def findCloseIndex(start: Int, content: String): Int = {
    // TODO: add tag name matching - make sure the same tag name opened as closed
    var tagOpen = false
    var comment = false
    var quoteOpen = false
    var selfClosing = false
    var closingTag = false
    var open = 0
    var p = '_'
    var pp = '_'
    var ppp = '_'
    for (i <- start until content.length) {
      val c = content.charAt(i)
      if (comment) {
        if (c == '>' && p == '-' && pp == '-') {
          comment = false
        }
      } else if (tagOpen) {
        if (c == '"') {
          quoteOpen = !quoteOpen
        } else if (!quoteOpen && c == '-' && p == '-' && pp == '!' && ppp == '<') {
          open -= 1
          tagOpen = false
          comment = true
        } else if (c == '/' && !quoteOpen) {
          if (p == '<') {
            closingTag = true
          } else {
            selfClosing = true
          }
        } else if (c == '>' && !quoteOpen) {
          if (selfClosing) {
            open -= 1
            if (open == 0) {
              return i + 1
            }
          } else if (closingTag) {
            open -= 2
            if (open == 0) {
              return i + 1
            }
          }
          tagOpen = false
          selfClosing = false
          closingTag = false
        }
      } else if (c == '<') {
        open += 1
        tagOpen = true
      } else {
        // Ignore text
      }
      ppp = pp
      pp = p
      p = c
    }
    throw new RuntimeException("No closing tag found: %s".format(content.substring(start)))
  }
}

class DynamicHTML(content: String) {
  private var _blocks = List[HTMLBlock](new StaticHTMLBlock(content))

  def blocks = _blocks

  def extract(id: String) = synchronized {
    _blocks.collectFirst {
      case dhb: DynamicHTMLBlock if dhb.id == id => dhb
    }.headOption match {
      case Some(dhb) => dhb
      case None => findBlockWithId(id) match {
        case Some(block) => {
          val (content, begin, end) = DynamicContent.extract(block.content, id)
          try {
            val builder = new SAXBuilder()
            val element = builder.build(new StringReader(content)).getRootElement
            var newBlocks = List.empty[HTMLBlock]
            if (end < block.content.length) {
              newBlocks = StaticHTMLBlock(block.content.substring(end)) :: newBlocks
            }
            val dhb = DynamicHTMLBlock(id, element, content)
            newBlocks = dhb :: newBlocks
            if (begin > 0) {
              newBlocks = StaticHTMLBlock(block.content.substring(0, begin)) :: newBlocks
            }
            _blocks = _blocks.patch(_blocks.indexOf(block), newBlocks, 1)
            dhb
          } catch {
            case exc: JDOMParseException => throw new RuntimeException("Unable to parse [%s]".format(content), exc)
          }
        }
        case None => throw new NullPointerException("Unable to find block with id: %s".format(id))
      }
    }
  }

  private def findBlockWithId(id: String) = _blocks.collectFirst {
    case shb: StaticHTMLBlock if (shb.content.indexOf("id=\"%s\"".format(id)) != -1) => shb
  }
}

trait HTMLBlock {
  def content: String
}

case class StaticHTMLBlock(content: String) extends HTMLBlock

case class DynamicHTMLBlock(id: String, element: Element, original: String, tag: HTMLTag = null) extends HTMLBlock {
  def content = if (tag != null) {
    tag.outputString
  } else {
    original
  }
}

abstract class Binder[T <: HTMLTag, V](implicit manifest: Manifest[V]) {
  val valueProperty = new Property[V]()(parent = null, manifest = manifest)

  final def bind(t: T, property: Property[_], hierarchy: String): Unit = {
    if (hierarchy == null) {
      property.asInstanceOf[Property[V]] bind valueProperty
      valueProperty bind property.asInstanceOf[Property[V]]
    } else {
      CaseClassBinding(property, hierarchy, valueProperty.asInstanceOf[Property[Any]])
    }
    println(s"Class: $getClass, T: $t, Hierarchy: $hierarchy")
    bind(t)
    Property.fireChanged(property)    // TODO: find a more efficient way to do this?
  }

  def bind(t: T): Unit
}