package com.skeleton.service

import akka.http.scaladsl.server.{ Directives, Route }
import com.skeleton.service.auth.AuthRoutes
import com.skeleton.service.errors.{ ErrorMapper, HttpError, InternalErrorHttp, ServiceError }
import com.skeleton.service.health.HealthRoutes
import com.skeleton.service.user.UserRoutes
import com.skeleton.utils.server.Version
import com.skeleton.utils.swagger.Swagger
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

trait Routes extends Version with Directives with FailFastCirceSupport

object Routes extends Directives {

  def buildRoutes(dependencies: Dependencies): Route =
    new UserRoutes(dependencies.userService).userRoutes ~
    new AuthRoutes(dependencies.authService).authRoutes ~
    new HealthRoutes(dependencies.healthService).healthRoutes ~
    Swagger.routes

  def buildErrorMapper(serviceErrorMapper: PartialFunction[ServiceError, HttpError]): ErrorMapper[ServiceError, HttpError] =
    (e: ServiceError) =>
      serviceErrorMapper
        .applyOrElse(
          e,
          (_: ServiceError) => InternalErrorHttp("Unexpected error")
        )
}
