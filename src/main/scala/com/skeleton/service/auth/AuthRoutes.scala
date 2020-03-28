package com.skeleton.service.auth

import java.util.UUID

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.{ StatusCode, StatusCodes }
import akka.http.scaladsl.server.Route
import com.skeleton.service.errors.{ ErrorMapper, ErrorResponse, HttpError, ServiceError }
import com.skeleton.service.swagger.SwaggerData._
import com.skeleton.service.user.UserModel.{ Token, UserCreate, UserDto, UserLogin, UserLoginDto }
import com.skeleton.service.{ Routes, SecuredRoutes }
import com.skeleton.utils.swagger.SwaggerSecurity
import io.circe.generic.auto._
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.{ Content, ExampleObject, Schema }
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.{ Tag, Tags }
import javax.ws.rs._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

@Tags(Array(new Tag(name = "Authentication")))
class AuthRoutes(val authService: AuthService) extends Routes with SecuredRoutes with SwaggerSecurity {

  val authorizationList = List("admin", "developer")

  //  val authRoutes: Route = auth.routes
  val authRoutes: Route = routes

  //  private object auth {

  implicit val httpErrorMapper: ErrorMapper[ServiceError, HttpError] =
    Routes.buildErrorMapper(ServiceError.httpErrorMapper)

  implicit class ErrorOps[E <: ServiceError, A](result: Future[Either[E, A]]) {
    def toRestError[G <: HttpError](implicit errorMapper: ErrorMapper[E, G]): Future[Either[G, A]] = result.map {
      case Left(error) => Left(errorMapper(error))
      case Right(value) => Right(value)
    }
  }

  def routes: Route =
    pathPrefix("api" / version)(
      authManagement
    )

  def authManagement: Route =
    pathPrefix("auth") {
      register ~ login ~ tokenManagement
    }

  @POST
  @Path("/api/v01/auth/register")
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @Operation(
    summary = "User registration",
    requestBody = new RequestBody(
      content = Array(
        new Content(
          schema = new Schema(
            name           = "UserCreate",
            title          = "UserCreate",
            implementation = classOf[UserCreate],
            required       = true
          ),
          examples = Array(
            new ExampleObject(
              name  = userName1,
              value = createUser1Json
            ),
            new ExampleObject(
              name  = userName2,
              value = createUser2JSON
            )
          )
        )
      )
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "201",
        description  = "Created",
        content = Array(
          new Content(
            schema = new Schema(implementation = classOf[UserDto]),
            examples = Array(
              new ExampleObject(
                name  = userName1,
                value = userDto1JSON
              ),
              new ExampleObject(
                name  = userName2,
                value = userDto2JSON
              )
            )
          )
        )
      ),
      new ApiResponse(
        responseCode = "409",
        description  = "Conflict",
        content = Array(
          new Content(
            schema = new Schema(name = "HttpError", implementation = classOf[HttpError]),
            examples = Array(
              new ExampleObject(
                value = recordAlreadyExists
              )
            )
          )
        )
      ),
      new ApiResponse(
        responseCode = "500",
        description  = "Internal server error",
        content = Array(
          new Content(
            schema = new Schema(name = "HttpError", implementation = classOf[HttpError]),
            examples = Array(
              new ExampleObject(
                value = internalError
              )
            )
          )
        )
      )
    )
  )
  def register: Route =
    path("register") {
      pathEndOrSingleSlash {
        post {
          entity(as[UserCreate]) { userRegister =>
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

  @POST
  @Path("/api/v01/auth/login")
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  @Operation(
    summary     = "User login",
    description = "",
    requestBody = new RequestBody(
      content = Array(
        new Content(
          schema = new Schema(
            name           = "UserLogin",
            title          = "UserLogin",
            implementation = classOf[UserLogin],
            required       = true
          ),
          examples = Array(
            new ExampleObject(
              name  = userName1,
              value = user1LoginJSON
            ),
            new ExampleObject(
              name  = userName2,
              value = user2LoginJSON
            )
          )
        )
      )
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description  = "Ok",
        content = Array(
          new Content(
            schema = new Schema(implementation = classOf[UserLoginDto]),
            examples = Array(
              new ExampleObject(
                name  = userName1,
                value = userLoginDto1JSON
              ),
              new ExampleObject(
                name  = userName2,
                value = userLoginDto2JSON
              )
            )
          )
        )
      ),
      new ApiResponse(
        responseCode = "404",
        description  = "NotFound",
        content = Array(
          new Content(
            schema = new Schema(name = "HttpError", implementation = classOf[HttpError]),
            examples = Array(
              new ExampleObject(
                value = notFound
              )
            )
          )
        )
      ),
      new ApiResponse(
        responseCode = "401",
        description  = "Unauthorized",
        content = Array(
          new Content(
            schema = new Schema(name = "HttpError", implementation = classOf[HttpError]),
            examples = Array(
              new ExampleObject(
                value = unauthorized
              )
            )
          )
        )
      ),
      new ApiResponse(
        responseCode = "500",
        description  = "Internal server error",
        content = Array(
          new Content(
            schema = new Schema(name = "HttpError", implementation = classOf[HttpError]),
            examples = Array(
              new ExampleObject(
                value = internalError
              )
            )
          )
        )
      )
    )
  )
  def login: Route =
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

  def tokenManagement: Route =
    pathPrefix("token") {
      authorized(authorizationList) { clms =>
        val claims = Map(
          "userId" -> clms("userId").toString,
          "role" -> clms("role").toString
        )
        getAccessToken(claims) ~ getRefreshToken(claims)
      }
    }

  @GET
  @Path("/api/v01/auth/token/access")
  @Produces(Array("application/json"))
  @SecurityRequirement(name = "bearerAuth")
  @Operation(
    summary     = "Get access token",
    description = "",
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description  = "Ok",
        content = Array(
          new Content(
            schema = new Schema(implementation = classOf[Token]),
            examples = Array(
              new ExampleObject(
                name  = userName1,
                value = user1LoginJSON
              ),
              new ExampleObject(
                name  = userName2,
                value = user2LoginJSON
              )
            )
          )
        )
      ),
      new ApiResponse(
        responseCode = "401",
        description  = "Unauthorized",
        content = Array(
          new Content(
            schema = new Schema(name = "HttpError", implementation = classOf[HttpError]),
            examples = Array(
              new ExampleObject(
                value = unauthorized
              )
            )
          )
        )
      ),
      new ApiResponse(
        responseCode = "500",
        description  = "Internal server error",
        content = Array(
          new Content(
            schema = new Schema(name = "HttpError", implementation = classOf[HttpError]),
            examples = Array(
              new ExampleObject(
                value = internalError
              )
            )
          )
        )
      )
    )
  )
  def getAccessToken(claims: Map[String, String]): Route =
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

  @GET
  @Path("/api/v01/auth/token/refresh")
  @Produces(Array("application/json"))
  @SecurityRequirement(name = "bearerAuth")
  @Operation(
    summary     = "Get refresh token",
    description = "",
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description  = "Ok",
        content = Array(
          new Content(
            schema = new Schema(implementation = classOf[Token]),
            examples = Array(
              new ExampleObject(
                name  = userName1,
                value = user1LoginJSON
              ),
              new ExampleObject(
                name  = userName2,
                value = user2LoginJSON
              )
            )
          )
        )
      ),
      new ApiResponse(
        responseCode = "401",
        description  = "Unauthorized",
        content = Array(
          new Content(
            schema = new Schema(name = "HttpError", implementation = classOf[HttpError]),
            examples = Array(
              new ExampleObject(
                value = unauthorized
              )
            )
          )
        )
      ),
      new ApiResponse(
        responseCode = "500",
        description  = "Internal server error",
        content = Array(
          new Content(
            schema = new Schema(name = "HttpError", implementation = classOf[HttpError]),
            examples = Array(
              new ExampleObject(
                value = internalError
              )
            )
          )
        )
      )
    )
  )
  def getRefreshToken(claims: Map[String, String]): Route =
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

  //  }

}
