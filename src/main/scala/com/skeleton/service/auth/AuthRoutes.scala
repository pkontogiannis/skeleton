package com.skeleton.service.auth

import java.util.UUID

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Route
import com.skeleton.service.errors.{ErrorMapper, ErrorResponse, HttpError, ServiceError}
import com.skeleton.service.user.UserModel.{UserCreate, UserLogin}
import com.skeleton.service.{Routes, SecuredRoutes}
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class AuthRoutes(val authService: AuthService) extends Routes with SecuredRoutes {

  val authorizationList = List("admin", "developer")

  val authRoutes: Route =
    auth.routes

  private object auth {

    implicit val httpErrorMapper: ErrorMapper[ServiceError, HttpError] =
      Routes.buildErrorMapper(ServiceError.httpErrorMapper)

    implicit class ErrorOps[E <: ServiceError, A](result: Future[Either[E, A]]) {
      def toRestError[G <: HttpError](implicit errorMapper: ErrorMapper[E, G]): Future[Either[G, A]] = result.map {
        case Left(error) => Left(errorMapper(error))
        case Right(value) => Right(value)
      }
    }

    def routes: Route = {
      pathPrefix("api" / version)(
        authManagement
      )
    }

    def authManagement: Route =
      pathPrefix("auth") {
        register ~ login ~ tokenManagement
      }

    def register: Route = {
      path("register") {
        pathEndOrSingleSlash {
          post {
            entity(as[UserCreate]) {
              userRegister =>
                onComplete(authService.registerUser(userRegister)) {
                  case Success(future) =>
                    completeEither(StatusCodes.Created, future)
                  case Failure(ex) =>
                    complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
                }
            }
          }
        }
      }
    }

    def login: Route = {
      path("login") {
        pathEndOrSingleSlash {
          post {
            entity(as[UserLogin]) { userLogin =>
              onComplete(authService.loginUser(userLogin)) {
                case Success(future) =>
                  completeEither(StatusCodes.OK, future)
                case Failure(ex) =>
                  complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
              }
            }
          }
        }
      }
    }

    def tokenManagement: Route =
      pathPrefix("token") {
        authorized(authorizationList) { clms =>
          val claims = Map("userId" -> clms("userId").toString, "role" -> clms("role").toString)
          getAccessToken(claims) ~ getRefreshToken(claims)
        }
      }

    def getAccessToken(claims: Map[String, String]): Route = {
      path("access") {
        pathEndOrSingleSlash {
          get {
            onComplete(authService.getAccessToken(UUID.fromString(claims("userId")), claims("role"))) {
              case Success(future) =>
                completeEither(StatusCodes.OK, future)
              case Failure(ex) =>
                complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
            }
          }
        }
      }
    }

    def completeEither[E <: ServiceError, R: ToEntityMarshaller]
    (statusCode: StatusCode, either: => Either[E, R])(
      implicit mapper: ErrorMapper[E, HttpError]
    ): Route = {
      either match {
        case Right(value) =>
          complete(statusCode, value)
        case Left(value) =>
          complete(value.statusCode, ErrorResponse(code = value.code, message = value.message))
      }
    }

    def getRefreshToken(claims: Map[String, String]): Route = {
      path("refresh") {
        pathEndOrSingleSlash {
          get {
            onComplete(authService.getRefreshToken(UUID.fromString(claims("userId")), claims("role"))) {
              case Success(future) =>
                completeEither(StatusCodes.OK, future)
              case Failure(ex) =>
                complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
            }
          }
        }
      }
    }

  }

}
