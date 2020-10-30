package com.skeleton.service.auth

import java.util.UUID

import com.skeleton.service.errors.DatabaseError
import com.skeleton.service.errors.ServiceError.AuthenticationError
import com.skeleton.service.user.UserModel.{Token, UserCreate, UserDto, UserLogin, UserLoginDto}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

trait AuthService extends LazyLogging {

  def loginUser(userLogin: UserLogin): Future[Either[AuthenticationError, UserLoginDto]]

  def registerUser(userRegister: UserCreate): Future[Either[DatabaseError, UserDto]]

  def getAccessToken(userId: UUID, role: String): Future[Either[AuthenticationError, Token]]

  def getRefreshToken(userId: UUID, role: String): Future[Either[AuthenticationError, Token]]

}
