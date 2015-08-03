package org.hyperscala.site


import org.hyperscala.BuildInfo
import org.hyperscala.bootstrap.Bootstrap
import org.hyperscala.bootstrap.component._
import org.hyperscala.css.SelectorStyleSheet
import org.hyperscala.javascript.JavaScriptString
import org.hyperscala.javascript.dsl.window
import org.hyperscala.selector.Selector
import org.hyperscala.ui.module.{GoogleTagManager, GoogleAnalytics}
import org.powerscala.Color
import org.hyperscala.html._
import attributes.Target
import org.hyperscala.css.attributes._
import org.hyperscala.web.Webpage
import language.reflectiveCalls
import org.hyperscala.web.module.FormSupport
import com.outr.net.http.session.MapSession

/**
 * @author Matt Hicks <matt@outr.com>
 */
class HyperscalaPage extends Webpage with FormSupport {
  def site = HyperscalaSite

  require(Bootstrap)
  require(new GoogleTagManager("GTM-5BNN4T"))

  title := "Hyperscala - Statically typed bare-metal HTML, CSS, and JavaScript framework for Scala."

  def sourceURL: String = null

  body.role := "document"

  head.contents += new tag.Link(rel = "stylesheet", href = "/css/style.css")

  new SelectorStyleSheet(Selector.element[tag.Body])(body) {
    paddingTop := 100.px
    paddingBottom := 30.px
  }

  body.contents += new NavBar {
    theme := NavBarTheme.Light

    val navigation = new NavBarNav {
      pullRight := true

      contents += new ListItem {
        contents += new tag.A(href = site.siteAbout.link, content = "About")
      }

      contents += new ListItem {
        contents += new tag.A(href = site.siteExamples.link, content = "Examples")
      }

      contents += new ListItem {
        contents += new tag.A(href = site.siteGenerator.link, content = "Generator")
      }

      contents += new ListItem {
        contents += new tag.A(href = site.siteDocumentation.link, content = "Documentation")
      }

      contents += new ListItem {
        contents += new tag.A(href = "https://github.com/darkfrog26/hyperscala/", content = "Project")
      }
    }

    val header = new NavBarHeader {
      contents += new NavBarToggle {
        contents += new tag.Span(clazz = List("sr-only")) {
          contents += "Toggle navigation"
        }
        contents += new tag.Span(clazz = List("icon-bar"))
        contents += new tag.Span(clazz = List("icon-bar"))
        contents += new tag.Span(clazz = List("icon-bar"))
        contents += new NavbarBrand {
          href := "/"
          contents += new tag.Img(id = "logo", src = "/images/hyperscala.png", alt = "Hyperscala")
        }
      }
    }

    val bar = new NavBarCollapse {
      contents += navigation
    }

    contents += new Container {
      contents += header
      contents += bar
    }
  }

  val main = new tag.Div
  val container = new Container {
    clazz += "wrapper"

    if (sourceURL != null) {
      val filename = sourceURL.substring(sourceURL.lastIndexOf('/') + 1)
      contents += new Button(s"View $filename on GitHub", buttonStyle = ButtonStyle.Primary) {
        style.float := Float.Right
        clickEvent := window.open(sourceURL, Target.Blank)
      }
    }
    contents += main
  }
  body.contents += container
  body.contents += new tag.Footer {
    contents += new tag.I {
      style.display := Display.Block
      style.width := 1170.px
      style.marginLeft := Length.Auto
      style.marginRight := Length.Auto
      style.color := Color.White
      style.fontWeight := FontWeight.Bold
      style.paddingBottom := 30.px
      style.fontSize := FontSize.Small
      style.textAlign := Alignment.Right
      contents += s"&copy;2015 Hyperscala.org, version: ${BuildInfo.version}, built: ${f"${BuildInfo.buildTime}%tc"}"
    }
  }
}