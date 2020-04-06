package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import com.skeleton.service.auth.{ AuthRoutes, AuthServiceDefault }
import com.skeleton.service.user.UserModel.{ UserCreate, UserDto, UserLogin, UserLoginDto }
import com.skeleton.service.user.persistence.UserPersistenceSQL
import io.circe.generic.auto._
import routes.helpers.{ ServiceSuite, ITTestData => itData }

class AuthRoutesIT extends ServiceSuite {

  trait Fixture {
    val userPersistence = new UserPersistenceSQL(dbAccess)
    userPersistence.deleteAllUsers()
    val authService       = new AuthServiceDefault(userPersistence)
    val authRoutes: Route = new AuthRoutes(authService).authRoutes
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

      val resultUser: UserDto = Post("/api/v01/auth/register", user) ~> authRoutes ~> check {
          handled shouldBe true
          status should ===(StatusCodes.Created)
          responseAs[UserDto]
        }

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

}
