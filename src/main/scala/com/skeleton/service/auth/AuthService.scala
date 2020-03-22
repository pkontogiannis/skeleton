package com.skeleton.service.auth

import com.skeleton.service.errors.DatabaseError
import com.skeleton.service.errors.ServiceError.AuthenticationError
import com.skeleton.service.user.UserModel.{Token, UserCreate, UserDto, UserLogin, UserLoginDto}

import scala.concurrent.Future


trait AuthService {

  def loginUser(userLogin: UserLogin): Future[Either[AuthenticationError, UserLoginDto]]

  def registerUser(userRegister: UserCreate): Future[Either[DatabaseError, UserDto]]

  def getAccessToken(userId: String, role: String): Future[Either[AuthenticationError, Token]]

  def getRefreshToken(userId: String, role: String): Future[Either[AuthenticationError, Token]]

}
