package com.skeleton.service.errors

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes._

sealed trait HttpError extends Serializable {
  val statusCode: StatusCode
  val code: String
  val message: String
}

case class MappingNotFoundErrorHttp() extends HttpError {
  val statusCode: StatusCode = NotFound
  val code: String           = "MappingNotFoundError"
  val message: String        = "MappingNotFoundError"
}

case class UnauthorizedErrorHttp() extends HttpError {
  val statusCode: StatusCode = Unauthorized
  val code: String           = "Unauthorized"
  val message: String        = "Unauthorized Operation"
}

case class ClientServiceErrorHttp() extends HttpError {
  override val statusCode: StatusCode = BadRequest
  override val code: String           = "badRequest"
  override val message: String        = "MappingNotFoundError"
}

case class RecordAlreadyExists() extends HttpError {
  override val statusCode: StatusCode = Conflict
  override val code: String           = "RecordAlreadyExists"
  override val message: String        = "This email already exists"
}

case class InternalErrorHttp(message: String) extends HttpError {
  override val statusCode: StatusCode = InternalServerError
  override val code: String           = "internalError"
}

case object DefaultNotFoundErrorHttp extends HttpError {
  override val statusCode: StatusCode = NotFound
  override val code                   = "DefaultNotFoundError"
  override val message                = "Can't find requested asset"
}

case class BadRequestErrorHttp(reason: String) extends HttpError {
  override val statusCode: StatusCode = BadRequest
  override val code                   = "BadRequestError"
  override val message                = s"A bad request because of $reason"
}
