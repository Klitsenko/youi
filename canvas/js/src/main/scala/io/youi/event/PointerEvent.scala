package io.youi.event

import io.youi.spatial.Point
import org.scalajs.dom.raw.MouseEvent

class PointerEvent private[event](val `type`: PointerEvent.Type,
                                  val local: Point,
                                  val global: Point,
                                  val htmlEvent: MouseEvent) extends Event {
  override def toString: String = s"PointerEvent(type: ${`type`}, local: $local, global: $global)"
}

object PointerEvent {
  def apply(`type`: Type, local: Point, global: Point, htmlEvent: MouseEvent): PointerEvent = {
    new PointerEvent(`type`, local, global, htmlEvent)
  }

  sealed trait Type

  object Type {
    case object Click extends Type
    case object DoubleClick extends Type
    case object Move extends Type
    case object Enter extends Type
    case object Exit extends Type
    case object Down extends Type
    case object Up extends Type
    case object Wheel extends Type
  }
}

class WheelEvent(local: Point,
                 global: Point,
                 val delta: WheelDelta,
                 override val htmlEvent: org.scalajs.dom.raw.WheelEvent)
  extends PointerEvent(PointerEvent.Type.Wheel, local, global, htmlEvent) {
  override def toString: String = s"WheelEvent(local: $local, global: $global, delta: $delta)"
}

object WheelEvent {
  def apply(local: Point, global: Point, delta: WheelDelta): WheelEvent = {
    new WheelEvent(local, global, delta, delta.htmlEvent)
  }
}