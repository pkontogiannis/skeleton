package com.skeleton.service.user

import java.util.UUID

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.{ StatusCode, StatusCodes }
import akka.http.scaladsl.server.Route
import cats.data.Validated
import com.skeleton.service.errors._
import com.skeleton.service.swagger.SwaggerData._
import com.skeleton.service.user.UserModel.{ UpdateUser, UserCreate, UserDto }
import com.skeleton.service.{ Routes, RoutesHelpers, SecuredRoutes }
import com.skeleton.utils.swagger.SwaggerSecurity
import io.circe.generic.auto._
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{ Content, ExampleObject, Schema }
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.{ Tag, Tags }
import io.swagger.v3.oas.annotations.{ Operation, Parameter }
import javax.ws.rs._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

@SecurityRequirement(name = "bearerAuth")
@Tags(Array(new Tag(name = "User")))
class UserRoutes(val userService: UserService) extends Routes with SecuredRoutes with SwaggerSecurity with RoutesHelpers {

  //  val userRoutes: Route = internal.routes
  val userRoutes: Route = routes
  val authorizationList = List("admin", "developer")

  //  private object internal {

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
      userManagement
    )

  def userManagement: Route =
    pathPrefix("users") {
      authorized(authorizationList) { clms =>
        val (connectedUserId, connectedUserRole) = extractClaims(clms)
        getMyUser(connectedUserId, connectedUserRole) ~
        userActions(connectedUserId, connectedUserRole) ~
        getUsers ~
        postUser
      }
    }

  @GET
  @Path("/api/v01/users/me")
  @Produces(Array("application/json"))
  @RequestBody(content = Array(new Content()))
  @Operation(
    summary     = "Get the list of users",
    description = "",
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description  = "Ok",
        content = Array(
          new Content(
            schema   = new Schema(implementation = classOf[UserDto]),
            examples = Array(new ExampleObject(value = userDto1JSON))
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
  def getMyUser(connectedUserId: UUID, connectedUserRole: String): Route =
    pathPrefix("me") {
      get(
        onComplete(userService.getUser(connectedUserId)) {
          case Success(future) => completeEither(StatusCodes.OK, future)
          case Failure(ex) =>
            complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
        }
      )
    }

  def extractClaims(claims: Map[String, Any]): (UUID, String) = {
    val connectedUserId   = UUID.fromString(claims("userId").toString)
    val connectedUserRole = claims("role").toString
    (connectedUserId, connectedUserRole)
  }

  @GET
  @Path("/api/v01/users")
  @Produces(Array("application/json"))
  @RequestBody(content = Array(new Content()))
  @Operation(
    summary     = "Get the list of users",
    description = "",
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description  = "Ok",
        content = Array(
          new Content(
            schema   = new Schema(implementation = classOf[UserDto]),
            examples = Array(new ExampleObject(value = listOfUsers))
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
  def getUsers: Route =
    get(
      onComplete(userService.getUsers) {
        case Success(future) => completeEither(StatusCodes.OK, future)
        case Failure(ex) =>
          complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
      }
    )

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
  @Path("/api/v01/users")
  @Produces(Array("application/json"))
  @Operation(
    summary     = "Post a user",
    description = "",
    requestBody = new RequestBody(
      content = Array(
        new Content(
          schema    = new Schema(implementation = classOf[UserCreate]),
          mediaType = "application/json",
          examples = Array(
            new ExampleObject(
              name  = "Petros",
              value = createUser1Json
            ),
            new ExampleObject(
              name  = "Manos",
              value = createUser2JSON
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
            schema   = new Schema(implementation = classOf[UserDto]),
            examples = Array(new ExampleObject(value = userDto1JSON))
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
  def postUser: Route =
    post {
      entity(as[UserCreate]) { userCreate =>
        userCreate.validate match {
          case Validated.Valid(validatedUserCreate) =>
            onComplete(userService.createUser(validatedUserCreate)) {
              case Success(future) =>
                completeEither(StatusCodes.Created, future)
              case Failure(ex) =>
                complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
            }
          case Validated.Invalid(e) =>
            completeWithValidationErrors(e)
        }
      }
    }

  def userActions(connectedUserId: UUID, connectedUserRole: String): Route =
    pathPrefix(Segment) { id =>
      val userId: UUID = UUID.fromString(id)
      getUser(userId) ~ putUser(userId) ~ deleteUser(connectedUserId, userId) ~ patchUser(userId)
    }

  @GET
  @Path("/api/v01/users/{userId}")
  @Produces(Array("application/json"))
  @Operation(
    summary     = "Get a user",
    description = "",
    parameters = Array(
      new Parameter(
        name     = "userId",
        in       = ParameterIn.PATH,
        required = true,
        schema   = new Schema(implementation = classOf[String]),
        example  = "123e4567-e89b-12d3-a456-426655440000"
      )
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description  = "Ok",
        content = Array(
          new Content(
            schema   = new Schema(implementation = classOf[UserDto]),
            examples = Array(new ExampleObject(value = userDto1JSON))
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
  def getUser(userId: UUID): Route =
    get(
      onComplete(userService.getUser(userId)) {
        case Success(future) => completeEither(StatusCodes.OK, future)
        case Failure(ex) =>
          complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
      }
    )

  @PUT
  @Path("/api/v01/users")
  @Produces(Array("application/json"))
  @Consumes(Array("application/json"))
  @Operation(
    summary     = "Update a user",
    description = "",
    parameters = Array(
      new Parameter(
        name     = "user id",
        in       = ParameterIn.PATH,
        required = true,
        schema   = new Schema(implementation = classOf[String]),
        example  = "123e4567-e89b-12d3-a456-426655440000"
      )
    ),
    requestBody = new RequestBody(
      content = Array(
        new Content(
          mediaType = "application/json",
          schema    = new Schema(implementation = classOf[UserCreate])
        )
      )
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description  = "Ok",
        content = Array(
          new Content(
            schema   = new Schema(implementation = classOf[UserDto]),
            examples = Array(new ExampleObject(value = userDto1JSON))
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
  def putUser(userId: UUID): Route =
    put {
      entity(as[UpdateUser]) { updateUser =>
        onComplete(userService.updateUser(userId, updateUser)) {
          case Success(future) => completeEither(StatusCodes.OK, future)
          case Failure(ex) =>
            complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
        }
      }
    }

  @PATCH
  @Path("/api/v01/users")
  @Produces(Array("application/json"))
  @Consumes(Array("application/json"))
  @Operation(
    summary     = "Partially update a user",
    description = "",
    parameters = Array(
      new Parameter(
        name     = "user id",
        in       = ParameterIn.PATH,
        required = true,
        schema   = new Schema(implementation = classOf[String])
      )
    ),
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[UserCreate])))),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description  = "Ok",
        content = Array(
          new Content(
            schema   = new Schema(implementation = classOf[UserDto]),
            examples = Array(new ExampleObject(value = userDto1JSON))
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
  def patchUser(userId: UUID): Route =
    patch {
      entity(as[UpdateUser]) { updateUser =>
        onComplete(userService.updateUserPartially(userId, updateUser)) {
          case Success(future) => completeEither(StatusCodes.OK, future)
          case Failure(ex) =>
            complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
        }
      }
    }

  @DELETE
  @Path("/api/v01/users")
  @Operation(
    summary     = "Delete a user",
    description = "",
    parameters = Array(
      new Parameter(
        name     = "user id",
        in       = ParameterIn.PATH,
        required = true,
        schema   = new Schema(implementation = classOf[String])
      )
    ),
    requestBody = new RequestBody(content = Array(new Content())),
    responses = Array(
      new ApiResponse(
        responseCode = "204",
        description  = "NoContent"
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
        responseCode = "405",
        description  = "Method Not Allowed",
        content      = Array(new Content(schema = new Schema(name = "HttpError", implementation = classOf[HttpError])))
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
  def deleteUser(connectedUserId: UUID, userId: UUID): Route =
    delete {
      onComplete(userService.deleteUser(userId, connectedUserId)) {
        case Success(future) => completeEither(StatusCodes.NoContent, future)
        case Failure(ex) =>
          complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
      }
    }

  //  }

}
