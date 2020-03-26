package com.skeleton.utils.jwt

import java.time.Clock
import java.util.UUID

import com.skeleton.service.errors.ServiceError
import com.skeleton.service.errors.ServiceError.AuthenticationError
import com.skeleton.service.user.UserModel.Token
import com.typesafe.config.{Config, ConfigFactory}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtCirce, JwtClaim}

import scala.util.{Failure, Success, Try}

object JWTUtils {

  val config: Config = ConfigFactory.load()

  private val tokenPrefix = config.getString("authentication.token.prefix")
  private val secretKey = config.getString("authentication.token.secret")
  private val algorithm = JwtAlgorithm.HS256
  private val acceptedAlgorithms = Seq(algorithm)
  private val accessTokenExpiration = config.getInt("authentication.token.access")
  private val refreshTokenExpiration = config.getInt("authentication.token.refresh")

  implicit val clock: Clock = Clock.systemUTC

  def getAccessToken(userId: String, role: String): Token = {
    val jwtClaim: JwtClaim = issueJWT(userId, role, accessTokenExpiration)
    val jwtToken = Jwt.encode(jwtClaim, secretKey, JwtAlgorithm.HS256)
    Token(s"$tokenPrefix$jwtToken", accessTokenExpiration)
  }

  def getRefreshToken(userId: String, role: String): Token = {
    val jwtClaim: JwtClaim = issueJWT(userId, role, refreshTokenExpiration)
    val jwtToken = Jwt.encode(jwtClaim, secretKey, JwtAlgorithm.HS256)
    Token(s"$tokenPrefix$jwtToken", refreshTokenExpiration)
  }

  private def issueJWT(userId: String, role: String, tokenExpiration: Int): JwtClaim = {
    JwtClaim(subject = Some(userId), issuer = Some(role)).issuedNow.expiresIn(tokenExpiration)
  }

  def validateToken(token: String): Either[AuthenticationError, Boolean] = {
    // If you only want to check if a token is valid without decoding it.
    // All good
    //    Jwt.validate(accessToken, secretKey, Seq(JwtAlgorithm.HS256))
    extractTokenBody(token) match {
      case Right(extractedToken) => tokenIsValid(extractedToken)
      case Left(_) => Left(AuthenticationError())
    }
  }

  def tokenIsValid(token: String): Either[ServiceError.AuthenticationError, Boolean] = {
    if (Jwt.isValid(token, secretKey, Seq(JwtAlgorithm.HS256))) {
      Right(true)
    } else {
      Left(AuthenticationError())
    }
  }

  def decodeToken(token: String): Either[AuthenticationError, Claims] = {
    val extractedToken: Either[AuthenticationError, String] = extractTokenBody(token)
    Jwt.decodeRaw(extractedToken.right.get, secretKey, acceptedAlgorithms) match {
      case Failure(_) => Left(AuthenticationError())
      case Success(jwtClaim) =>
        Claims(JwtCirce.parseClaim(jwtClaim)) match {
          case Left(error) => Left(error)
          case Right(claims) =>
            Right(claims)
        }
    }
  }

  def extractTokenBody(token: String): Either[AuthenticationError, String] = token match {
    case tok if tok.startsWith(tokenPrefix) =>
      Right(tok.substring(7))
    case _ => Left(AuthenticationError())
  }

  def extractClaims(token: String): Option[Claims] = {
    JwtCirce.decode(token, secretKey, Seq(algorithm)).toOption.flatMap { c =>
      for {
        userId <- c.subject.flatMap(s => Try(UUID.fromString(s.toString)).toOption)
        expiration <- c.expiration.filter(_ > currentTimeSeconds)
        issuedAt <- c.issuedAt.filter(_ <= System.currentTimeMillis())
        role <- c.issuer
      } yield Claims(userId, issuedAt, expiration, role)
    }
  }

  private def currentTimeSeconds: Long = System.currentTimeMillis() / 1000

}
