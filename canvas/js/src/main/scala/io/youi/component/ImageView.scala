package io.youi.component

import io.youi.{Context, LazyFuture}
import io.youi.image.Image
import io.youi.theme.ImageViewTheme
import org.scalajs.dom.File
import reactify.Var

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ImageView extends Component {
  def this(file: File) = {
    this()
    load(file)
  }
  def this(source: String) = {
    this()
    load(source)
  }

  override lazy val theme: Var[ImageViewTheme] = Var(ImageView)

  val image: Var[Image] = prop(Image.empty, updatesRendering = true)

  override def draw(context: Context): Future[Unit] = super.draw(context).flatMap { _ =>
    image().drawImage(context, size.width(), size.height())
  }

  size.measured.width := image.width
  size.measured.height := image.height

  def load(file: File): Future[Image] = Image.fromFile(file).map { image =>
    this.image := image
    image
  }

  def load(source: String): Future[Image] = Image(source).map { image =>
    this.image := image
    image
  }
}

object ImageView extends ImageViewTheme