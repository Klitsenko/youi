package io.youi.component

import io.youi.event._
import io.youi.spatial.Point
import io.youi.theme.RendererTheme
import io.youi.{AnimationFrame, Cursor, Drawer, HTMLEvents}
import org.scalajs.dom.raw.MouseEvent
import org.scalajs.dom.{document, html, raw}
import reactify._

class Renderer(canvas: html.Canvas) extends Container with RendererTheme {
  override protected[youi] lazy val drawer: Drawer = new Drawer(canvas, swapCanvases = false)

  override lazy val theme: Var[_ <: RendererTheme] = Var(Renderer)
  val htmlEvents: HTMLEvents = new HTMLEvents(document.body)
  override lazy val renderer: Val[Option[Renderer]] = Val(Some(this))
  override val globalVisibility: Val[Boolean] = visible
  val pointerTarget: Var[Option[Component]] = Var(None)

  htmlEvents.click.attach(pointerEvent(_, PointerEvent.Type.Click))
  htmlEvents.doubleClick.attach(pointerEvent(_, PointerEvent.Type.DoubleClick))
  htmlEvents.mouse.down.attach(pointerEvent(_, PointerEvent.Type.Down))
  htmlEvents.mouse.up.attach(pointerEvent(_, PointerEvent.Type.Up))
  htmlEvents.mouse.move.attach(pointerEvent(_, PointerEvent.Type.Move))
  htmlEvents.mouse.cancel.attach(pointerEvent(_, PointerEvent.Type.Cancel))
  Mouse.wheel.attach(wheelEvent)
  cursor := pointerTarget().map(_.cursor()).getOrElse(Cursor.Auto)      // Renderer's cursor should reflect the pointer target's cursor
  cursor.attach(c => canvas.style.cursor = c.value)

  private val globalPoint = Point.mutable()
  private def pointerEvent(evt: MouseEvent, `type`: PointerEvent.Type): Unit = if (visible()) {
    val rect = canvas.getBoundingClientRect()
    globalPoint.set(evt.clientX - rect.left, evt.clientY - rect.top)
    val result = hitTest(globalPoint) match {
      case HitResult.Miss => None
      case HitResult.Hit(l, c) => Some(l -> c)
    }
    val newTarget = result.map(_._2)
    if (pointerTarget() != newTarget) {
      pointerTarget().foreach { component =>
        val local = component.localize(globalPoint)
        component.event.pointer := PointerEvent(PointerEvent.Type.Exit, local.x, local.y, globalPoint.x, globalPoint.y, evt)
      }
      pointerTarget := newTarget
      pointerTarget().foreach { component =>
        val local = component.localize(globalPoint)
        component.event.pointer := PointerEvent(PointerEvent.Type.Enter, local.x, local.y, globalPoint.x, globalPoint.y, evt)
      }
    }
    result.foreach {
      case (local, component) => component.event.pointer := PointerEvent(`type`, local.x, local.y, globalPoint.x, globalPoint.y, evt)
    }
  }
  private def wheelEvent(delta: WheelDelta): Unit = if (visible()) {
    val rect = canvas.getBoundingClientRect()
    globalPoint.set(delta.htmlEvent.clientX - rect.left, delta.htmlEvent.clientY - rect.top)
    hitTest(globalPoint) match {
      case HitResult.Miss => // Nothing
      case HitResult.Hit(local, component) => {
        component.event.pointer := WheelEvent(local.x, local.y, globalPoint.x, globalPoint.y, delta)
      }
    }
  }

  override protected def defaultThemeParent = Some(theme)

  visible.attach {
    case true => canvas.style.display = "block"
    case false => canvas.style.display = "none"
  }

  if (Option(canvas.parentElement).isEmpty) {
    canvas.style.position = "absolute"
    canvas.style.left = "0px"
    canvas.style.top = "0px"
    visible := false

    document.body.appendChild(canvas)

  }
  AnimationFrame.delta.attach(update)

  def apply(): html.Canvas = canvas
}

object Renderer extends RendererTheme