package com.skeleton.service.building

import com.skeleton.service.building.persistence.BuildingPersistence
import com.skeleton.service.errors.{DatabaseError, ServiceError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BuildingServiceDefault(val buildingPersistence: BuildingPersistence) extends BuildingService {
  def getBuildings: Future[Either[DatabaseError, List[BuildingModel.BuildingDto]]] = ???

  def getBuilding(buildingId: Int): Future[Either[DatabaseError, BuildingModel.BuildingDto]] = ???

  def createBuilding(buildingCreate: BuildingModel.BuildingCreate): Future[Either[DatabaseError, BuildingModel.BuildingDto]] =
    buildingPersistence.createBuilding(buildingCreate).map {
      case Right(value) =>
        logger.info(s"[${this.getClass.getSimpleName}] successfully created a building with id: ${value.buildingId}")
        Right(BuildingModel.buildingToBuildingDto(value))
      case Left(error) =>
        Left(error)
    }

  def updateBuilding(
                      buildingId: Int,
                      updateBuilding: BuildingModel.BuildingUpdate
                    ): Future[Either[DatabaseError, BuildingModel.BuildingDto]] = ???

  def updateBuildingPartially(
                               buildingId: Int,
                               updateBuilding: BuildingModel.BuildingUpdate
                             ): Future[Either[DatabaseError, BuildingModel.BuildingDto]] = ???

  def deleteBuilding(buildingId: Int, connectedBuildingId: Int): Future[Either[ServiceError, Boolean]] = ???

  def deleteAllBuildings(): Future[Either[DatabaseError, Boolean]] = ???
}
