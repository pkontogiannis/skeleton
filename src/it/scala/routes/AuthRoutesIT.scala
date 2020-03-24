package routes

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import com.skeleton.service.auth.{AuthRoutes, AuthServiceDefault}
import com.skeleton.service.user.UserModel.{UserCreate, UserDto, UserLogin, UserLoginDto}
import com.skeleton.service.user.persistence.UserPersistenceSQL
import com.skeleton.utils.database.DBAccess
import io.circe.generic.auto._
import routes.helpers.ServiceSuite

import scala.collection.JavaConverters._

class AuthRoutesIT extends ServiceSuite {

  private val roles: List[String] = config.getStringList("authentication.roles").asScala.toList
  val user: UserCreate = UserCreate("pkont4@gmail.com", "Petros", "Kontogiannis", "password", roles.head)
  val expectedUser: UserDto = UserDto(UUID.randomUUID().toString, "pkont4@gmail.com", "Petros", "Kontogiannis", roles.head)
  val userLogin: UserLogin = UserLogin(user.email, user.password)

  trait Fixture {
    val dbAccess: DBAccess = DBAccess(system)
    val userPersistence = new UserPersistenceSQL(dbAccess)
    userPersistence.deleteAllUsers
    val authService = new AuthServiceDefault(userPersistence)
    val authRoutes: Route = new AuthRoutes(authService).authRoutes
  }

  "Auth Routes" should {

    "successfully register a user" in new Fixture {
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
