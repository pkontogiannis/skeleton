package routes.helpers

import java.util.UUID

import com.skeleton.service.user.UserModel.{ UserCreate, UserDto }
import com.typesafe.config.{ Config, ConfigFactory }

import scala.jdk.CollectionConverters._
import scala.util.Random

object ITTestData {

  val config: Config = ConfigFactory.load()

  val roles: List[String] = config.getStringList("authentication.roles").asScala.toList

  val userCreate1: UserCreate = UserCreate(
    email     = generateRandomEmail,
    firstName = "Petros",
    lastName  = "Kon",
    password  = "passw0rd",
    role      = roles.head
  )

  val userCreate2: UserCreate = UserCreate(
    email     = generateRandomEmail,
    firstName = "Manos",
    lastName  = "Pal",
    password  = "passw0rd",
    role      = roles.head
  )

  val expectedUser: UserDto =
    UserDto(
      userId    = UUID.randomUUID(),
      email     = userCreate1.email,
      firstName = "Petros",
      lastName  = "Kon",
      role      = roles.head
    )

  def generateRandomEmail: String = Random.alphanumeric.filter(_.isLetter).take(10).mkString("") + "@" + "skeleton.com"

}
