package org.hyperscala.ui.widgets

import org.hyperscala.module._
import org.powerscala.Version
import org.hyperscala.web.site.{Website, Webpage}
import org.hyperscala.html.tag
import com.outr.webcommunicator.netty.handler.PathHandler
import org.hyperscala.realtime.Realtime

/**
 * @author Matt Hicks <matt@outr.com>
 */
object CKEditor extends Module {
  def name = "ckeditor"

  def version = Version(4, 2, 0)

  override def dependencies = List(Realtime)

  def init() = {
    Website().register(PathHandler("/cke/", "ckeditor/"))
  }

  def load() = {
    val page = Webpage()
    page.head.contents += new tag.Script(mimeType = "text/javascript", src = "/cke/ckeditor.js")
    page.head.contents += new tag.Script(mimeType = "text/javascript", src = "/cke/richeditor.js")
  }
}