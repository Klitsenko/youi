package org.hyperscala.ui

import org.hyperscala.module.Module
import org.powerscala.Version
import org.hyperscala.web.{Webpage, Website}
import org.hyperscala.html._
import org.hyperscala.jquery.jQuery
import scala.collection.mutable.ListBuffer

/**
 * @author Matt Hicks <matt@outr.com>
 */
class jsTree {

}

object jsTree extends Module {
  var debug = false
  val name = "jstree"
  val version = Version(2, extra = "alpha")

  override val dependencies = List(jQuery.LatestWithDefault)

  def init() = {
    Website().register(PathHandler("/js/jstree/", "jstree/"))
  }

  def load() = Webpage().head.contents += new tag.Script(mimeType = "text/javascript", src = "/js/jstree/jquery.jstree.js")

  def apply(t: HTMLTag, types: Type*) = {
    Webpage().require(jsTree)
    val attributes = ListBuffer.empty[String]
    if (types.nonEmpty) {
      attributes +=
        """
          |'types': {
          | 'valid_children': [ 'default', 'leaf' ],
          | 'types': {
          |%s
          | }
          |}
        """.stripMargin.trim.format(types.map(t => "\t\t\t%s".format(t.toJSON)).mkString(",\r\n"))
    }
    attributes += "'plugins': ['themes', 'html_data', 'ui', 'types']"
    val js =
      """
        |jstree({
        |%s
        |})
      """.stripMargin.trim.format(attributes.map(s => "\t\t%s".format(s)).mkString(",\r\n"))
    jQuery.call(t, js)
  }

  case class Type(name: String,
                  maxChildren: Int = -1,
                  maxDepth: Int = -1,
                  useData: Boolean = false,
                  iconURL: String = null,
                  allowSelect: Boolean = true,
                  hoverNode: Boolean = true,
                  typeAttr: String = "role") {
    def toJSON = {
      val attributes = ListBuffer.empty[String]
      if (maxChildren != -1) {
        attributes += "'max_children': %s".format(maxChildren)
      }
      if (maxDepth != -1) {
        attributes += "'max_depth': %s".format(maxDepth)
      }
      if (useData) {
        attributes += "'useData': %s".format(useData)
      }
      if (iconURL != null) {
        attributes += "'icon': { 'image': '%s' }".format(iconURL)
      }
      if (!allowSelect) {
        attributes += "'select_node': function() { console.log('select!'); return false; }"
      }
      if (!hoverNode) {
        attributes += "'hover_node': false"
      }
      attributes += "'type_attr': '%s'".format(typeAttr)
      """
        |'%s': {
        |%s
        |}
      """.stripMargin.trim.format(name, attributes.map(s => "\t\t\t\t%s".format(s)).mkString(",\r\n"))
    }
  }
}