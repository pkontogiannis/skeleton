package com.skeleton.service.building

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.skeleton.service.Routes.extractClaims
import com.skeleton.service.building.BuildingModel.BuildingCreate
import com.skeleton.service.{Routes, SecuredRoutes}
import com.skeleton.utils.swagger.SwaggerSecurity
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.{Tag, Tags}

import scala.util.{Failure, Success}

@SecurityRequirement(name = "bearerAuth")
@Tags(Array(new Tag(name = "User")))
class BuildingRoutes(val buildingService: BuildingService) extends Routes with SecuredRoutes with SwaggerSecurity {

  val buildingRoutes: Route = routes
  val authorizationList = List("admin", "developer")

  def routes: Route =
    pathPrefix("api" / version)(
      buildingManagement
    )

  def buildingManagement: Route =
    pathPrefix("buildings") {
      authorized(authorizationList) { clms =>
        val (connectedUserId, connectedUserRole) = extractClaims(clms)
        //        getMyBuildings(connectedUserId, connectedUserRole) ~
        //        buildingActions ~
        postBuilding
      }
    }

  def postBuilding: Route =
    post {
      entity(as[BuildingCreate]) { buildingCreate =>
        onComplete(buildingService.createBuilding(buildingCreate)) {
          case Success(future) =>
            completeEither(StatusCodes.Created, future)
          case Failure(ex) =>
            complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
        }
      }
    }

  //  def buildingActions(connectedUserId: UUID, connectedUserRole: String): Route =
  //    pathPrefix(Segment) { buildingId =>
  //      getBuilding(buildingId) ~ putBuilding(buildingId) ~
  //      deleteBuilding(connectedUserId, buildingId) ~ patchBuilding(buildingId)
  //    }

}
