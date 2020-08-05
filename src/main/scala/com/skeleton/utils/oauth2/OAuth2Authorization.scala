package com.skeleton.utils.oauth2

import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Directive1, _ }

import scala.jdk.javaapi.CollectionConverters
import scala.util.{ Failure, Success }

class OAuth2Authorization(tokenVerifier: OauthTokenVerifier) {

  def authorizeTokenWithRole(roles: List[String]): Directive1[VerifiedToken] =
    authorized flatMap {
      case t if t.roles.contains(roles) => provide(t)
      case _ => reject(AuthorizationFailedRejection).toDirective[Tuple1[VerifiedToken]]
    }

  def authorized: Directive1[VerifiedToken] =
    extractCredentials.flatMap {
      case Some(OAuth2BearerToken(token)) =>
        onComplete(tokenVerifier.verifyToken(token)).flatMap {
          case Failure(_) => reject(AuthorizationFailedRejection).toDirective[Tuple1[VerifiedToken]]
          case Success(f) =>
            f match {
              case Left(_) => reject(AuthorizationFailedRejection)
              case Right(t) =>
                provide(
                  VerifiedToken(
                    token,
                    t.getId,
                    t.getName,
                    t.getPreferredUsername,
                    t.getEmail,
                    CollectionConverters.asScala(t.getRealmAccess.getRoles).toSeq
                  )
                )
            }
        }
      case None => reject(AuthorizationFailedRejection)
    }

}
