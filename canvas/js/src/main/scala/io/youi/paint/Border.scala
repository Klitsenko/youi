package io.youi.paint

import io.youi.path.Path
import io.youi.{Compass, Context}

trait Border {
  def paint: Paint
  def size(compass: Compass): Double
  def draw(width: Double, height: Double, context: Context, fill: Paint): Unit
  def width: Double = size(Compass.West) + size(Compass.East)
  def height: Double = size(Compass.North) + size(Compass.South)
}

object Border {
  lazy val empty: Border = apply(Stroke.none)

  def apply(stroke: Stroke, radius: Double = 0.0): RectangleBorder = RectangleBorder(stroke, radius)
}

// TODO: CompoundBorder

// TODO: PaddingBorder

case class RectangleBorder(stroke: Stroke, radius: Double) extends Border {
  override def paint: Paint = stroke.paint

  override def size(compass: Compass): Double = stroke.lineWidth

  override def draw(width: Double, height: Double, context: Context, fill: Paint): Unit = if (fill.nonEmpty || stroke.nonEmpty) {
    if (stroke.lineWidth == 0.0 && radius == 0.0) {
      context.rect(0.0, 0.0, width, height)
    } else {
      val sizeAdjust = stroke.lineWidth
      if (radius == 0.0) {
        context.rect(Path.fix(sizeAdjust), Path.fix(sizeAdjust), Path.fix(width - sizeAdjust) - 1.0, Path.fix(height - sizeAdjust) - 1.0)
      } else {
        context.roundedRect(Path.fix(sizeAdjust), Path.fix(sizeAdjust), Path.fix(width - sizeAdjust) - 1.0, Path.fix(height - sizeAdjust) - 1.0, radius)
      }
    }
    if (fill.nonEmpty) {
      context.fill(fill, apply = true)
    }
    if (stroke.nonEmpty) {
      context.stroke(stroke, apply = true)
    }
  }
}