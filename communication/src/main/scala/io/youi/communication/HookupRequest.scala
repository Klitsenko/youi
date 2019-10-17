package io.youi.communication

import io.circe.Json

import scala.concurrent.Promise

case class HookupRequest(id: Long, `type`: MessageType, json: Json, promise: Promise[Json]) {
  def success(response: Json): Unit = if (!promise.isCompleted) promise.success(response)
  def failure(throwable: Throwable): Unit = if (!promise.isCompleted) promise.failure(throwable)
}