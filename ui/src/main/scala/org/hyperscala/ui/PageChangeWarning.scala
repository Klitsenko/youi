package org.hyperscala.ui

import org.hyperscala.module._
import org.hyperscala.web.{Website, Webpage}
import org.powerscala.Version
import org.hyperscala.html._
import org.hyperscala.javascript.JavaScriptString
import org.hyperscala.realtime.Realtime

/**
 * @author Matt Hicks <mhicks@outr.com>
 */
object PageChangeWarning extends Module {
  def name = "pagechangewarning"

  def version = Version(1)

  override def dependencies = List(Realtime)

  def init() = {
    Website().register("/js/page_change_warning.js", "page_change_warning.js")
  }

  def load() = {
    Webpage().head.contents += new tag.Script(src = "/js/page_change_warning.js")
  }

  def warn(message: String) = {
    val m = message match {
      case null => "null"
      case _ => "'%s'".format(message)
    }
    Webpage().body.contents += new tag.Script {
      contents += new JavaScriptString("setPageChangeWarning(%s);".format(m))
    }
  }
}
