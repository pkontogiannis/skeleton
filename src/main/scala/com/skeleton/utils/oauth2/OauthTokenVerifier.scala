package com.skeleton.utils.oauth2

import com.skeleton.service.errors.ServiceError.AuthenticationError
import org.keycloak.representations.AccessToken

import scala.concurrent.Future

trait OauthTokenVerifier {
  def verifyToken(token: String): Future[Either[AuthenticationError, AccessToken]]
}
