package com.skeleton.service.errors

import com.skeleton.service.errors.ServiceError.AuthenticationError

trait ErrorMapper[-FromError <: ServiceError, +ToError <: HttpError] extends (FromError => ToError)

object ErrorMapper {

  implicit val toHttpError: ErrorMapper[AuthenticationError, UnauthorizedErrorHttp] = { _: ServiceError.AuthenticationError =>
    UnauthorizedErrorHttp()
  }
}
