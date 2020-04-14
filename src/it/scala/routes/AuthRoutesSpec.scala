package routes

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server._
import com.skeleton.service.auth.{ AuthRoutes, AuthService }
import com.skeleton.service.user.UserModel.{ Token, UserCreate, UserDto, UserLogin, UserLoginDto }
import com.skeleton.utils.jwt.JWTUtils
import io.circe.generic.auto._
import routes.helpers.{ ServiceSuite, ITTestData => itData }

class AuthRoutesSpec extends ServiceSuite {

  trait Fixture {
    dependencies.userService.deleteAllUsers()
    val authService: AuthService = dependencies.authService
    val authRoutes: Route        = new AuthRoutes(authService).authRoutes
  }

  "Auth Routes" should {

    "successfully register a user" in new Fixture {
      val user: UserCreate      = itData.userCreate1
      val expectedUser: UserDto = itData.expectedUser(user)

      Post("/api/v01/auth/register", user) ~> authRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultUser: UserDto = responseAs[UserDto]
        assert(
          resultUser.email === expectedUser.email
        )
      }
    }

    "successfully login user" in new Fixture {
      val user: UserCreate      = itData.userCreate1
      val userLogin: UserLogin  = UserLogin(user.email, user.password)
      val expectedUser: UserDto = itData.expectedUser(user)

      Post("/api/v01/auth/register", user) ~> authRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)

        Post("/api/v01/auth/login", userLogin) ~> authRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.OK)
          val resultUser: UserLoginDto = responseAs[UserLoginDto]
          assert(
            resultUser.email === expectedUser.email
          )
        }
      }
    }

    "correctly failed to login a user" in new Fixture {
      val user: UserCreate     = itData.userCreate1
      val userLogin: UserLogin = UserLogin(user.email, "dummyPassword")

      Post("/api/v01/auth/register", user) ~> authRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        Post("/api/v01/auth/login", userLogin) ~> authRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.Unauthorized)
        }
      }
    }

    "successfully generate refresh token" in new Fixture {
      val user: UserCreate     = itData.userCreate1
      val userLogin: UserLogin = UserLogin(user.email, user.password)

      Post("/api/v01/auth/register", user) ~> authRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)

        Post("/api/v01/auth/login", userLogin) ~> authRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.OK)
          val resultUser: UserLoginDto = responseAs[UserLoginDto]
          val jwtToken: String         = resultUser.refreshToken.token.split(" ")(1)

          Get("/api/v01/auth/token/refresh") ~> addCredentials(OAuth2BearerToken(jwtToken)) ~>
          authRoutes ~> check {
            handled shouldBe true
            status should ===(StatusCodes.OK)
            val token: Token = responseAs[Token]
            assert(JWTUtils.validateToken(token.token) == Right(true))
          }
        }
      }
    }

    "successfully generate access token" in new Fixture {
      val user: UserCreate     = itData.userCreate1
      val userLogin: UserLogin = UserLogin(user.email, user.password)

      Post("/api/v01/auth/register", user) ~> authRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)

        Post("/api/v01/auth/login", userLogin) ~> authRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.OK)
          val resultUser: UserLoginDto = responseAs[UserLoginDto]
          val jwtToken: String         = resultUser.refreshToken.token.split(" ")(1)

          Get("/api/v01/auth/token/access") ~> addCredentials(OAuth2BearerToken(jwtToken)) ~>
          authRoutes ~> check {
            handled shouldBe true
            status should ===(StatusCodes.OK)
            val token: Token = responseAs[Token]
            assert(JWTUtils.validateToken(token.token) == Right(true))
          }
        }
      }
    }

    "successfully failed to generate refresh token for non existent user" in new Fixture {
      val jwtToken: Token = JWTUtils.getRefreshToken(UUID.randomUUID(), itData.roles.head)
      Get("/api/v01/auth/token/refresh") ~> addCredentials(OAuth2BearerToken(jwtToken.token)) ~>
      authRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Unauthorized)
      }
    }

    "successfully failed to generate access token for non existent user" in new Fixture {
      val jwtToken: Token = JWTUtils.getAccessToken(UUID.randomUUID(), itData.roles.head)
      Get("/api/v01/auth/token/access") ~> addCredentials(OAuth2BearerToken(jwtToken.token)) ~>
      authRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Unauthorized)
      }
    }

    "successfully unauthorized a request without header" in new Fixture {
      Get("/api/v01/auth/token/access") ~> authRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Unauthorized)
      }
    }
  }

}
