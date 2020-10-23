package com.skeleton.service

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Route
import cats.data.NonEmptyChain
import com.skeleton.service.errors.{ DomainValidation, ErrorResponse }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._

trait RoutesHelpers extends FailFastCirceSupport {

  def completeWithValidationErrors(e: NonEmptyChain[DomainValidation]): Route =
    complete(
      StatusCodes.BadRequest,
      ErrorResponse(
        code = "InvalidData",
        message = e.toNonEmptyList.toList
          .mkString(", ")
      )
    )

}
