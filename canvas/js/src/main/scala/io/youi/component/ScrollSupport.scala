package io.youi.component

import io.youi.paint.{Paint, Stroke}
import io.youi.{Color, Context}
import reactify._

trait ScrollSupport extends Component {
  object scroll {
    object horizontal {
      val enabled: Var[Boolean] = Var(false)
      val bar: Var[ScrollBar] = Var(ScrollBar.None)
      def apply(delta: Double): Unit = {
        val value = offset.x + delta
        val min = 0
        val max = size.measured.width() - size.width()
        offset.x.static(math.max(max, math.min(min, value)))
      }
    }
    object vertical {
      val enabled: Var[Boolean] = Var(true)
      val bar: Var[ScrollBar] = Var(ScrollBar.None)
      def apply(delta: Double): Unit = {
        val value = offset.y + delta
        val min = 0
        val max = -(size.measured.height() - size.height())
        offset.y.static(math.max(max, math.min(min, value)))
      }
    }
  }

  // Wheel support
  event.pointer.wheel.attach { evt =>
    if (scroll.vertical.enabled()) scroll.vertical(-evt.delta.y)
    if (scroll.horizontal.enabled()) scroll.horizontal(evt.delta.x)

    evt.stopPropagation()
    evt.preventDefault()
  }

  event.gestures.pointers.dragged.attach { pointer =>
    if (scroll.vertical.enabled()) scroll.vertical(pointer.deltaY)
  }

  override protected def postDraw(context: Context): Unit = {
    super.postDraw(context)


    val height = (size.height / size.measured.height) * size.height
    val width = (size.width / size.measured.width) * size.width
    val x = (-offset.x / (size.measured.width - size.width)) * (size.width - width)
    val y = (-offset.y / (size.measured.height - size.height)) * (size.height - height)
    scroll.horizontal.bar.draw(this, context, Axis.Horizontal, x, width)
    scroll.vertical.bar.draw(this, context, Axis.Vertical, y, height)
  }
}

sealed trait Axis

object Axis {
  case object Horizontal extends Axis
  case object Vertical extends Axis
}

trait ScrollBar {
  def draw(component: ScrollSupport, context: Context, axis: Axis, offset: Double, thickness: Double): Unit
}

class SimpleScrollBar(stroke: Stroke, fill: Paint, size: Double) extends ScrollBar {
  override def draw(component: ScrollSupport, context: Context, axis: Axis, offset: Double, thickness: Double): Unit = {
    context.begin()
    axis match {
      case Axis.Horizontal => context.rect(offset, component.size.height - size - 5.0, thickness, size)
      case Axis.Vertical => context.rect(component.size.width - size - 5.0, offset, size, thickness)
    }
    context.close()
    context.fill(fill, apply = true)
    context.stroke(stroke, apply = true)
  }
}

object ScrollBar {
  object None extends ScrollBar {
    override def draw(component: ScrollSupport, context: Context, axis: Axis, offset: Double, thickness: Double): Unit = {}
  }
  def simple(stroke: Stroke = Stroke.none,
             fill: Paint = Color.Black,
             thickness: Double = 20.0): SimpleScrollBar = new SimpleScrollBar(stroke, fill, thickness)
}