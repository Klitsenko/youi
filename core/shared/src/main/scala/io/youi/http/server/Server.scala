package io.youi.http.server

import com.outr.scribe.Logging
import io.youi.http.{HttpRequest, HttpResponse}
import pl.metastack.metarx.Sub

import scala.collection.immutable.TreeSet

trait Server extends HttpHandler with Logging {
  val config = new ServerConfig(this)

  object handlers {
    /**
      * The error handler if an error is thrown. This is used automatically when an HttpHandler fires a Throwable but
      * can be explicitly used for more specific errors. The error handler is responsible for applying an existing
      * status on the HttpResponse or setting one if the status is a non-error.
      *
      * Defaults to DefaultErrorHandler
      */
    val error: Sub[ErrorHandler] = Sub(DefaultErrorHandler)

    private var set = TreeSet.empty[HttpHandler]

    def +=(handler: HttpHandler): Unit = synchronized {
      set += handler
    }
    def -=(handler: HttpHandler): Unit = synchronized {
      set -= handler
    }

    def apply(): Set[HttpHandler] = set
  }

  protected val implementation: ServerImplementation

  def isRunning: Boolean = implementation.isRunning

  def start(): Unit = synchronized {
    implementation.start()
  }

  def stop(): Unit = synchronized {
    implementation.stop()
  }

  def restart(): Unit = synchronized {
    stop()
    start()
  }

  def error(t: Throwable): Unit = logger.error(t)

  def errorSupport[R](f: => R): R = try {
    f
  } catch {
    case t: Throwable => {
      error(t)
      throw t
    }
  }

  override def handle(request: HttpRequest, response: HttpResponse): HttpResponse = {
    var updated = response
    try {
      handlers().foreach { handler =>
        updated = handler.handle(request, updated)
      }
      updated
    } catch {
      case t: Throwable => {
        error(t)
        handlers.error.get.handle(request, updated, Some(t))
      }
    }
  }
}