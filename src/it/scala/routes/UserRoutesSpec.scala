package routes

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server._
import com.skeleton.service.auth.{ AuthRoutes, AuthService }
import com.skeleton.service.errors.ErrorResponse
import com.skeleton.service.user.UserModel.{ Token, UpdateUser, UserCreate, UserDto, UserLogin, UserLoginDto }
import com.skeleton.service.user.{ UserRoutes, UserService }
import com.skeleton.utils.jwt.JWTUtils
import io.circe.generic.auto._
import routes.helpers.{ ServiceSuite, ITTestData => itData }

class UserRoutesSpec extends ServiceSuite {

  trait Fixture {
    dependencies.userService.deleteAllUsers()
    val userService: UserService = dependencies.userService
    val authService: AuthService = dependencies.authService
    val userRoutes: Route        = new UserRoutes(userService).userRoutes
    val authRoutes: Route        = new AuthRoutes(authService).authRoutes
  }

  "User routes" should {

    "successfully creates a user" in new Fixture {
      val user: UserCreate      = itData.userCreate1
      val expectedUser: UserDto = itData.expectedUser(user)
      val accessToken: Token    = JWTUtils.getAccessToken(UUID.randomUUID(), itData.roles.head)

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultUser: UserDto = responseAs[UserDto]
        assert(
          resultUser.email === expectedUser.email
        )
      }
    }

    "successfully creates a user 1" in new Fixture {
      val user: UserCreate      = itData.userCreate1
      val expectedUser: UserDto = itData.expectedUser(user)
      val accessToken: Token    = JWTUtils.getAccessToken(UUID.randomUUID(), itData.roles.head)

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
      val user: UserCreate      = itData.userCreate1
      val expectedUser: UserDto = itData.expectedUser(user)
      val accessToken: Token    = JWTUtils.getAccessToken(UUID.randomUUID(), itData.roles.head)

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)

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
    }

    "successfully serves a user" in new Fixture {
      val user: UserCreate      = itData.userCreate1
      val expectedUser: UserDto = itData.expectedUser(user)
      val accessToken: Token    = JWTUtils.getAccessToken(UUID.randomUUID(), itData.roles.head)

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultUser: UserDto = responseAs[UserDto]

        Get("/api/v01/users/" + resultUser.userId) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.OK)
          val result: UserDto = responseAs[UserDto]
          assert(
            result.email === expectedUser.email
          )
        }
      }
    }

    "successfully serves a list of users" in new Fixture {
      val user: UserCreate      = itData.userCreate1
      val user2: UserCreate     = itData.userCreate2
      val expectedUser: UserDto = itData.expectedUser(user)
      val accessToken: Token    = JWTUtils.getAccessToken(UUID.randomUUID(), itData.roles.head)

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[UserDto]

        Post("/api/v01/users", user2) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.Created)

          Get("/api/v01/users") ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
            handled shouldBe true
            status should ===(StatusCodes.OK)
            responseAs[List[UserDto]].length shouldBe 2
          }
        }
      }
    }

    "successfully handles an not-existent user" in new Fixture {
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), itData.roles.head)

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
      val user: UserCreate   = itData.userCreate1
      val accessToken: Token = JWTUtils.getAccessToken(userId = UUID.randomUUID(), role = itData.roles.head)

      val updateUser: UpdateUser = UpdateUser(
        userId    = None,
        email     = Some("pkont4@gmail.com"),
        password  = None,
        firstName = None,
        lastName  = None,
        role      = None
      )

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultUser: UserDto = responseAs[UserDto]

        Put("/api/v01/users/" + resultUser.userId, updateUser) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.OK)
          val result: UserDto = responseAs[UserDto]
          assert(
            result.email === updateUser.email.get &&
            result.firstName === ""
          )
        }
      }
    }

    "successfully partially updates a user with different email" in new Fixture {
      val user: UserCreate   = itData.userCreate1
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), itData.roles.head)
      val updateUser: UpdateUser = UpdateUser(
        userId    = None,
        email     = Some("pkont4@gmail.com"),
        firstName = Some("Isidor"),
        password  = None,
        lastName  = None,
        role      = None
      )

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultUser: UserDto = responseAs[UserDto]

        Patch("/api/v01/users/" + resultUser.userId, updateUser) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.OK)
          val result: UserDto = responseAs[UserDto]
          assert(
            result.firstName === updateUser.firstName.get
          )
        }
      }
    }

    "successfully partially update a use with the same email" in new Fixture {
      val user: UserCreate   = itData.userCreate1
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), itData.roles.head)
      val updateUser: UpdateUser = UpdateUser(
        userId    = None,
        email     = Some(user.email),
        firstName = Some("Isidor"),
        password  = None,
        lastName  = None,
        role      = None
      )

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultUser: UserDto = responseAs[UserDto]

        Patch("/api/v01/users/" + resultUser.userId, updateUser) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.OK)
          val result: UserDto = responseAs[UserDto]
          assert(
            result.firstName === updateUser.firstName.get
          )
        }
      }
    }

    "failed to partially update a user" in new Fixture {
      val user1: UserCreate  = itData.userCreate1
      val user2: UserCreate  = itData.userCreate1
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), itData.roles.head)
      val updateUser: UpdateUser = UpdateUser(
        userId    = None,
        email     = Some(user1.email),
        firstName = Some("Isidor"),
        password  = None,
        lastName  = None,
        role      = None
      )

      Post("/api/v01/users", user1) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)

        Post("/api/v01/users", user2) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.Created)
          val resultUser: UserDto = responseAs[UserDto]

          Patch("/api/v01/users/" + resultUser.userId, updateUser) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
            handled shouldBe true
            status should ===(StatusCodes.Conflict)
          }
        }
      }
    }

    "successfully deletes a user" in new Fixture {
      val user: UserCreate   = itData.userCreate1
      val accessToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), itData.roles.head)

      Post("/api/v01/users", user) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultUser: UserDto = responseAs[UserDto]

        Delete("/api/v01/users/" + resultUser.userId) ~> RawHeader("Authorization", accessToken.token) ~> userRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.NoContent)
        }
      }
    }

    "successfully not allowed user to delete himself" in new Fixture {
      val user: UserCreate      = itData.userCreate1
      val userLogin: UserLogin  = UserLogin(user.email, user.password)
      val expectedUser: UserDto = itData.expectedUser(user)

      Post("/api/v01/auth/register", user) ~> authRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultUser: UserDto = responseAs[UserDto]

        Post("/api/v01/auth/login", userLogin) ~> authRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.OK)
          val resultLoginUser: UserLoginDto = responseAs[UserLoginDto]
          assert(
            resultLoginUser.email === expectedUser.email
          )

          Delete("/api/v01/users/" + resultUser.userId) ~> RawHeader("Authorization", resultLoginUser.accessToken.token) ~>
          userRoutes ~> check {
            handled shouldBe true
            status should ===(StatusCodes.MethodNotAllowed)
            val errorResponse: ErrorResponse = responseAs[ErrorResponse]
            assert(
              errorResponse.code === "MethodNotAllowedErrorHttp" &&
              errorResponse.message === "User cannot delete himself"
            )
          }
        }
      }
    }

  }
}
