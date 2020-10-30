package com.skeleton.service.auth

import java.util.UUID

import com.skeleton.service.errors.DatabaseError
import com.skeleton.service.errors.ServiceError.AuthenticationError
import com.skeleton.service.user.UserModel
import com.skeleton.service.user.UserModel.{Token, UserCreate, UserDto, UserLogin, UserLoginDto}
import com.skeleton.service.user.persistence.UserPersistence
import com.skeleton.utils.jwt.JWTUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthServiceDefault(val userPersistence: UserPersistence) extends AuthService {

  def loginUser(userLogin: UserLogin): Future[Either[AuthenticationError, UserLoginDto]] =
    userPersistence.loginUser(userLogin.email, userLogin.password).map {
      case Right(user) =>
        val refreshToken = JWTUtils.getRefreshToken(user.userId, user.role)
        val accessToken  = JWTUtils.getAccessToken(user.userId, user.role)
        logger.info(s"[${this.getClass.getSimpleName}] successfully login user with uuid: ${user.userId}")
        Right(
          UserLoginDto(
            user.email,
            accessToken,
            refreshToken,
            user.role,
            "Bearer"
          )
        )
      case Left(_) =>
        logger.info(s"[${this.getClass.getSimpleName}] failed try login user with email: ${userLogin.email}")
        Left(AuthenticationError())
    }

  def registerUser(userRegister: UserCreate): Future[Either[DatabaseError, UserDto]] =
    userPersistence.createUser(userRegister).map {
      case Right(value) =>
        logger.info(s"[${this.getClass.getSimpleName}] successfully created a user with uuid: ${value.userId}")
        Right(UserModel.userToUserDto(value))
      case Left(error) =>
        Left(error)
    }

  def getAccessToken(userId: UUID, role: String): Future[Either[AuthenticationError, Token]] =
    Future(
      Right(
        JWTUtils.getAccessToken(userId, role)
      )
    )

  def getRefreshToken(userId: UUID, role: String): Future[Either[AuthenticationError, Token]] =
    userPersistence.getUser(userId).map {
      case Left(_) => Left(AuthenticationError())
      case Right(_) =>
        Right(
          JWTUtils.getRefreshToken(userId, role)
        )
    }

}
