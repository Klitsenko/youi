package io.youi.component

import com.outr.pixijs.PIXI
import io.youi.net.URL
import io.youi.{History, dom}
import org.scalajs.dom.html

class Video extends Image {
  private val element = {
    val v = dom.create[html.Video]("video")
    v.autoplay = false
    v
  }

  private val baseTexture = PIXI.VideoBaseTexture.fromVideo(element)
  baseTexture.autoPlay = false

  texture := new Texture(new PIXI.Texture(baseTexture))

  def this(url: URL) = {
    this()

    element.src = url.toString
  }

  def this(path: String) = {
    this(History.url().withPath(path))
  }

  def play(): Unit = element.play()
  def pause(): Unit = element.pause()
  def isPaused: Boolean = element.paused
}