package com.skeleton.utils.oauth2

import java.util.concurrent.ForkJoinPool

import com.skeleton.service.errors.ServiceError.AuthenticationError
import org.keycloak.TokenVerifier
import org.keycloak.adapters.KeycloakDeployment
import org.keycloak.common.VerificationException
import org.keycloak.representations.AccessToken

import scala.concurrent.{ ExecutionContext, ExecutionContextExecutor, Future }

class KeycloakTokenVerifier(keycloakDeployment: KeycloakDeployment) extends OauthTokenVerifier {
  implicit val executionContext: ExecutionContextExecutor = ExecutionContext.fromExecutor(new ForkJoinPool(2))

  override def verifyToken(token: String): Future[Either[AuthenticationError, AccessToken]] =
    Future {
      try {
        val at = TokenVerifier.create(token, classOf[AccessToken]).verify().getToken
        Right(at)
      } catch {
        case _: VerificationException => Left(AuthenticationError())
      }
    }
}
