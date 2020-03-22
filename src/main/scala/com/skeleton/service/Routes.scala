package com.skeleton.service

import akka.http.scaladsl.server.{Directives, Route}
import com.skeleton.service.errors.{ErrorMapper, HttpError, InternalErrorHttp, ServiceError}
import com.skeleton.utils.server.Version
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport


trait Routes extends Version
  with Directives with FailFastCirceSupport

object Routes extends Directives {

  def buildRoutes(dependencies: Dependencies): Route = ???

  def buildErrorMapper(serviceErrorMapper: PartialFunction[ServiceError, HttpError]): ErrorMapper[ServiceError, HttpError] =
    (e: ServiceError) =>
      serviceErrorMapper
        .applyOrElse(e, (_: ServiceError) => InternalErrorHttp("Unexpected error"))

}
