package org.hyperscala.examples.basic

import org.hyperscala.examples.Example
import org.hyperscala.html._
import org.hyperscala.web.Webpage
import org.powerscala.IO

/**
 * @author Matt Hicks <mhicks@outr.com>
 */
class StaticHTMLExample extends Webpage with Example {
  body.contents += new tag.Div {
    contents += "Static content below:"
    contents += new StaticHTML(IO.copy(getClass.getClassLoader.getResource("static.html")))
  }
}
