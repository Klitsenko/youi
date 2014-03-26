package org.hyperscala.ui.widgets

import org.hyperscala.module._
import org.hyperscala.web._
import org.powerscala.Version

import org.hyperscala.html._
import org.hyperscala.javascript.JavaScriptString
import org.hyperscala.jquery.jQuery
import com.outr.net.http.session.Session

/**
 * @author Matt Hicks <matt@outr.com>
 */
class NivoSlider(_id: String,
                 theme: String = "default",
                 effect: NivoEffect = NivoEffect.Random,
                 slices: Int = 15,
                 boxCols: Int = 8,
                 boxRows: Int = 4,
                 animationSpeed: Int = 500,
                 pauseTime: Int = 3000,
                 startSlide: Int = 0,
                 directionNav: Boolean = true,
                 controlNav: Boolean = true,
                 controlNavThumbs: Boolean = false,
                 pauseOnHover: Boolean = true,
                 manualAdvance: Boolean = false,
                 prevText: String = "Prev",
                 nextText: String = "Next",
                 randomStart: Boolean = false) extends tag.Div(id = _id, clazz = List("slider-wrapper", "theme-%s".format(theme))) {
  this.require(NivoSlider)

  val slider = new tag.Div(id = "%sSlider".format(_id), clazz = List("nivoSlider"))

  contents += slider

  contents += new tag.Script(mimeType = "text/javascript") {
    contents += new JavaScriptString(
      """
        |$(window).load(function () {
        | $('#%s').nivoSlider({
        |   effect: '%s',
        |   slices: %s,
        |   boxCols: %s,
        |   boxRows: %s,
        |   animSpeed: %s,
        |   pauseTime: %s,
        |   startSlide: %s,
        |   directionNav: %s,
        |   controlNav: %s,
        |   controlNavThumbs: %s,
        |   pauseOnHover: %s,
        |   manualAdvance: %s,
        |   prevText: '%s',
        |   nextText: '%s',
        |   randomStart: %s
        | });
        |});
      """.stripMargin.format(slider.id(), effect.value, slices, boxCols, boxRows, animationSpeed, pauseTime, startSlide, directionNav, controlNav, controlNavThumbs, pauseOnHover, manualAdvance, prevText, nextText, randomStart))
  }
}

object NivoSlider extends Module {
  def name = "nivo-slider"

  def version = Version(3, 2)

  override def dependencies = List(InterfaceWithDefault(jQuery, jQuery.Latest))

  override def init[S <: Session](website: Website[S]) = {
    website.addClassPath("/nivo-slider/", "nivo-slider/")
  }

  override def load[S <: Session](page: Webpage[S]) = {
    page.head.contents += new tag.Link(rel = "stylesheet", href = "/nivo-slider/themes/default/default.css", mimeType = "text/css", media = "screen")
    page.head.contents += new tag.Link(rel = "stylesheet", href = "/nivo-slider/themes/light/light.css", mimeType = "text/css", media = "screen")
    page.head.contents += new tag.Link(rel = "stylesheet", href = "/nivo-slider/themes/dark/dark.css", mimeType = "text/css", media = "screen")
    page.head.contents += new tag.Link(rel = "stylesheet", href = "/nivo-slider/themes/bar/bar.css", mimeType = "text/css", media = "screen")
    page.head.contents += new tag.Link(rel = "stylesheet", href = "/nivo-slider/nivo-slider.css", mimeType = "text/css", media = "screen")
    page.head.contents += new tag.Script(mimeType = "text/javascript", src = "/nivo-slider/jquery.nivo.slider.pack.js")
  }
}
