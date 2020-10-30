package com.skeleton.service

import java.util.UUID

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.{Directives, Route}
import com.skeleton.service.auth.AuthRoutes
import com.skeleton.service.errors.{ErrorMapper, HttpError, ServiceError, _}
import com.skeleton.service.health.HealthRoutes
import com.skeleton.service.user.UserRoutes
import com.skeleton.utils.server.Version
import com.skeleton.utils.swagger.Swagger
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Routes extends Version with Directives with FailFastCirceSupport {

  def completeEither[E <: ServiceError, R: ToEntityMarshaller](statusCode: StatusCode, either: => Either[E, R])(
    implicit mapper: ErrorMapper[E, HttpError]
  ): Route =
    either match {
      case Right(value) =>
        complete(statusCode, value)
      case Left(value) =>
        complete(
          value.statusCode,
          ErrorResponse(code = value.code, message = value.message)
        )
    }

  implicit val httpErrorMapper: ErrorMapper[ServiceError, HttpError] =
    Routes.buildErrorMapper(ServiceError.httpErrorMapper)

  implicit class ErrorOps[E <: ServiceError, A](result: Future[Either[E, A]]) {
    def toRestError[G <: HttpError](implicit errorMapper: ErrorMapper[E, G]): Future[Either[G, A]] = result.map {
      case Left(error) => Left(errorMapper(error))
      case Right(value) => Right(value)
    }
  }

}

object Routes extends Directives {

  def extractClaims(claims: Map[String, Any]): (UUID, String) = {
    val connectedUserId = UUID.fromString(claims("userId").toString)
    val connectedUserRole = claims("role").toString
    (connectedUserId, connectedUserRole)
  }

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
