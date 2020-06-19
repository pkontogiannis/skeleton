package com.skeleton.service.health

import akka.http.scaladsl.model.StatusCodes

object HealthModel {

  case class HealthStatus(status: Int, state: String)

  object HealthStatus {

    val OK: HealthStatus   = HealthStatus(StatusCodes.OK.intValue, "OK")
    val DOWN: HealthStatus = HealthStatus(StatusCodes.InternalServerError.intValue, "DOWN")
    val INIT: HealthStatus = HealthStatus(
      StatusCodes.ServiceUnavailable.intValue,
      "INITIALIZING"
    )

  }

}
