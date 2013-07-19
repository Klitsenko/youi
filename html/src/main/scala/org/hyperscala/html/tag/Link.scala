package org.hyperscala.html.tag

import org.hyperscala._
import css.StyleSheet
import html.HTMLTag
import org.hyperscala.html.attributes._
import org.hyperscala.html.constraints._

/**
 * NOTE: This file has been generated. Do not modify directly!
 * @author Matt Hicks <mhicks@hyperscala.org>
 */
class Link extends Textual with BodyChild with HeadChild with HTMLTag {
  lazy val xmlLabel = "link"

  def this(name: String = null,
           accessKey: java.lang.Character = null,
           clazz: List[String] = null,
           contentEditable: ContentEditable = null,
           contextMenu: String = null,
           dir: Direction = null,
           draggable: Draggable = null,
           dropZone: DropZone = null,
           hidden: java.lang.Boolean = null,
           id: String = null,
           lang: String = null,
           role: String = null,
           spellCheck: java.lang.Boolean = null,
           style: StyleSheet = null,
           tabIndex: java.lang.Integer = null,
           titleText: String = null,
           href: String = null,
           hrefLang: String = null,
           media: String = null,
           mimeType: String = null,
           rel: String = null,
           sizes: String = null,
           content: String = null) = {
    this()
    init(name, accessKey, clazz, contentEditable, contextMenu, dir, draggable, dropZone, hidden, id, lang, role, spellCheck, style, tabIndex, titleText)
    up(this.href, href)
    up(this.hrefLang, hrefLang)
    up(this.media, media)
    up(this.mimeType, mimeType)
    up(this.rel, rel)
    up(this.sizes, sizes)
    up(this.content, content)
  }

  lazy val href = PropertyAttribute[String]("href", null)
  lazy val hrefLang = PropertyAttribute[String]("hreflang", null)
  lazy val media = PropertyAttribute[String]("media", null)
  val mimeType = PropertyAttribute[String]("type", "text/css", inclusion = InclusionMode.Always)
  val rel = PropertyAttribute[String]("rel", "stylesheet", inclusion = InclusionMode.Always)
  lazy val sizes = PropertyAttribute[String]("sizes", null)
}