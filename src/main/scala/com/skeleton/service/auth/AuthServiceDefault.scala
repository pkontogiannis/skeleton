package com.skeleton.service.auth

import com.skeleton.service.errors.DatabaseError
import com.skeleton.service.errors.ServiceError.AuthenticationError
import com.skeleton.service.user.UserModel
import com.skeleton.service.user.UserModel.{Token, UserCreate, UserDto, UserLogin, UserLoginDto}
import com.skeleton.service.user.persistence.UserPersistence
import com.skeleton.utils.jwt.JWTUtils
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthServiceDefault(val userPersistence: UserPersistence)
  extends AuthService with LazyLogging {

  import logger._

  def loginUser(userLogin: UserLogin): Future[Either[AuthenticationError, UserLoginDto]] = {
    userPersistence.loginUser(userLogin.email, userLogin.password).map {
      case Right(user) =>
        val refreshToken = JWTUtils.getRefreshToken(user.userId, user.role)
        val accessToken = JWTUtils.getAccessToken(user.userId, user.role)
        Right(UserLoginDto(user.email, accessToken, refreshToken,
          user.role, "Bearer"))
      case Left(_) => Left(AuthenticationError())
    }
  }

  def registerUser(userRegister: UserCreate): Future[Either[DatabaseError, UserDto]] = {
    userPersistence.createUser(userRegister).map {
      case Right(value) =>
        info(s"[UserService] successfully created a user with uuid: ${value.userId}")
        Right(UserModel.userToUserDto(value))
      case Left(error) =>
        Left(error)
    }
  }

  def getAccessToken(userId: String, role: String): Future[Either[AuthenticationError, Token]] =
    Future(Right(
      JWTUtils.getAccessToken(userId, role)
    ))

  def getRefreshToken(userId: String, role: String): Future[Either[AuthenticationError, Token]] =
    Future(Right(
      JWTUtils.getRefreshToken(userId, role)
    ))
}
