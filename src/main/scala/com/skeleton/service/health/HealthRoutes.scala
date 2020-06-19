package com.skeleton.service.health

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.skeleton.service.Routes
import com.skeleton.service.health.HealthModel.HealthStatus

class HealthRoutes(val healthService: HealthService) extends Routes {

  val healthRoutes: Route = routes

  def routes: Route =
    pathPrefix("health") {
      live ~ ready
    }

  def live: Route =
    path("live") {
      pathEndOrSingleSlash {
        get {
          complete(StatusCodes.OK)
        }
      }
    }

  def ready: Route =
    path("ready") {
      pathEndOrSingleSlash {
        get {
          complete(HealthStatus.unapply(healthService.ready).get)
        }
      }
    }
}
