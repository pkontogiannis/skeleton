package com.skeleton.service.health

import akka.http.scaladsl.model.StatusCodes
import io.circe.Encoder

object HealthModel {

  case class HealthStatus(status: Int, state: String)

  object HealthStatus {

    implicit val encodeFieldType: Encoder[HealthStatus] =
      Encoder.forProduct2("status", "state")(HealthStatus.unapply(_).get)

    val OK   = HealthStatus(StatusCodes.OK.intValue, "OK")
    val DOWN = HealthStatus(StatusCodes.InternalServerError.intValue, "DOWN")
    val INIT = HealthStatus(
      StatusCodes.ServiceUnavailable.intValue,
      "INITIALIZING"
    )

  }

}
