package com.skeleton.service.health

import akka.http.scaladsl.server.Route
import com.skeleton.service.Routes

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
          complete(healthService.live)
        }
      }
    }

  def ready: Route =
    path("ready") {
      pathEndOrSingleSlash {
        get {
          complete(healthService.ready)
        }
      }
    }
}
