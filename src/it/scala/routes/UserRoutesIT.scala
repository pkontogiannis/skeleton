package routes

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server._
import com.skeleton.service.errors.ErrorResponse
import com.skeleton.service.user.UserModel.{Token, UpdateUser, UserCreate, UserDto}
import com.skeleton.service.user.persistence.UserPersistenceSQL
import com.skeleton.service.user.{UserRoutes, UserServiceDefault}
import com.skeleton.utils.database.DBAccess
import com.skeleton.utils.jwt.JWTUtils
import io.circe.generic.auto._
import routes.helpers.ServiceSuite

import scala.collection.JavaConverters._

class UserRoutesIT extends ServiceSuite {

  private val roles: List[String] = config.getStringList("authentication.roles").asScala.toList

  trait Fixture {
    val dbAccess: DBAccess = DBAccess(system)
    val userPersistence = new UserPersistenceSQL(dbAccess)
    userPersistence.deleteAllUsers()
    val userService = new UserServiceDefault(userPersistence)
    val userRoutes: Route = new UserRoutes(userService).userRoutes
  }

  "User routes" should {

    "successfully creates a user" in new Fixture {
      val user: UserCreate = UserCreate("pkont4@gmail.com", "Petros", "Kontogiannis", "password", roles.head)
      val expectedUser: UserDto = UserDto(UUID.randomUUID(), "pkont4@gmail.com", "Petros", "Kontogiannis", roles.head)
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), roles.head)

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultUser: UserDto = responseAs[UserDto]
        assert(
          resultUser.email === expectedUser.email
        )
      }
    }

    "successfully handles a user with an existent email" in new Fixture {
      val user: UserCreate = UserCreate("pkont4@gmail.com", "Petros", "Kontogiannis", "password", roles.head)
      val expectedUser: UserDto = UserDto(UUID.randomUUID(), "pkont4@gmail.com", "Petros", "Kontogiannis", roles.head)
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), roles.head)

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
      }

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Conflict)
        val errorResponse: ErrorResponse = responseAs[ErrorResponse]
        assert(
          errorResponse.code === "RecordAlreadyExists" &&
            errorResponse.message === "This email already exists"
        )
      }
    }

    "successfully serves a user" in new Fixture {
      val user: UserCreate = UserCreate("pkont4@gmail.com", "Petros", "Kontogiannis", "password", roles.head)
      val expectedUser: UserDto = UserDto(UUID.randomUUID(), "pkont4@gmail.com", "Petros", "Kontogiannis", roles.head)
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), roles.head)

      val resultUser: UserDto = Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[UserDto]
      }
      Get("/api/v01/users/" + resultUser.userId) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        val result: UserDto = responseAs[UserDto]
        assert(
          result.email === expectedUser.email
        )
      }
    }

    "successfully serves a list of users" in new Fixture {
      val user: UserCreate = UserCreate("pkont4@gmail.com", "Petros", "Kontogiannis", "password", roles.head)
      val user2: UserCreate = UserCreate("pkont5@gmail.com", "Petros", "Kontogiannis", "password", roles.head)
      val expectedUser: UserDto = UserDto(UUID.randomUUID(), "pkont4@gmail.com", "Petros", "Kontogiannis", roles.head)
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), roles.head)

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[UserDto]
      }
      Post("/api/v01/users", user2) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
      }
      Get("/api/v01/users") ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        responseAs[List[UserDto]].length shouldBe 2
      }
    }

    "successfully handles an not-existent user" in new Fixture {
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), roles.head)

      Get("/api/v01/users/" + UUID.randomUUID()) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.NotFound)
        val errorResponse: ErrorResponse = responseAs[ErrorResponse]
        assert(
          errorResponse.code === "DefaultNotFoundError" &&
            errorResponse.message === "Can't find requested asset"
        )
      }
    }

    "successfully updates a user" in new Fixture {
      val user: UserCreate = UserCreate("pkont4@gmail.com", "Petros", "Kontogiannis", "password", roles.head)
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), roles.head)

      val updateUser: UpdateUser = UpdateUser(
        None, Some("pkont4@gmail.com"), None, None, None, None
      )

      val resultUser: UserDto = Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[UserDto]
      }

      Put("/api/v01/users/" + resultUser.userId, updateUser) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        val result: UserDto = responseAs[UserDto]
        assert(
          result.firstName === ""
        )
      }
    }

//    "successfully partially updates a user" in new Fixture {
//      val user: UserCreate = UserCreate("pkont4@gmail.com", "Petros", "Kontogiannis", "password", roles.head)
//      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), roles.head)
//      val updateUser: UpdateUser = UpdateUser(
//        userId = None, email = Some("pkont4@gmail.com"), firstName = Some("Isidor"),
//        password = None, lastName = None, role = None
//      )
//
//      val resultUser: UserDto = Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
//        handled shouldBe true
//        status should ===(StatusCodes.Created)
//        responseAs[UserDto]
//      }
//
//      Patch("/api/v01/users/" + resultUser.userId, updateUser) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
//        handled shouldBe true
//        status should ===(StatusCodes.OK)
//        val result: UserDto = responseAs[UserDto]
//        assert(
//          result.firstName === updateUser.firstName.get
//        )
//      }
//    }

    "successfully deletes a user" in new Fixture {
      val user: UserCreate = UserCreate("pkont4@gmail.com", "Petros", "Kontogiannis", "password", roles.head)
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), roles.head)

      val resultUser: UserDto = Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[UserDto]
      }

      Delete("/api/v01/users/" + resultUser.userId) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.NoContent)
      }
    }

  }
}
