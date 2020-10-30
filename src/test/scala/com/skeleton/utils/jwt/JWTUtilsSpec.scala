package com.skeleton.utils.jwt

import java.util.UUID

import com.skeleton.service.user.UserModel
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.funspec.AnyFunSpecLike

import scala.jdk.CollectionConverters._

class JWTUtilsSpec extends AnyFunSpecLike {

  val config: Config                      = ConfigFactory.load()
  val userId: UUID                        = UUID.randomUUID()
  val roles: List[String]                 = config.getStringList("authentication.roles").asScala.toList
  private val accessTokenExpiration: Int  = config.getInt("authentication.token.access")
  private val refreshTokenExpiration: Int = config.getInt("authentication.token.refresh")

  describe("JWT") {
    it("Successfully generate an Access Token") {
      val accessToken: UserModel.Token = JWTUtils.getAccessToken(userId, roles.head)
      assert(accessToken.token.startsWith("Bearer "))
      assert(accessToken.expiresIn == accessTokenExpiration)
    }
    it("Successfully generate an Refresh Token") {
      val accessToken: UserModel.Token = JWTUtils.getRefreshToken(userId, roles.head)
      assert(accessToken.token.startsWith("Bearer "))
      assert(accessToken.expiresIn == refreshTokenExpiration)
    }

  }

}
