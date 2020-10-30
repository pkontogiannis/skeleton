package com.skeleton.service.building

import com.skeleton.service.building.BuildingModel.{BuildingCreate, BuildingDto, BuildingUpdate}
import com.skeleton.service.errors.{DatabaseError, ServiceError}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

trait BuildingService extends LazyLogging {

  def getBuildings: Future[Either[DatabaseError, List[BuildingDto]]]

  def getBuilding(buildingId: Int): Future[Either[DatabaseError, BuildingDto]]

  def createBuilding(buildingCreate: BuildingCreate): Future[Either[DatabaseError, BuildingDto]]

  def updateBuilding(buildingId: Int, updateBuilding: BuildingUpdate): Future[Either[DatabaseError, BuildingDto]]

  def updateBuildingPartially(buildingId: Int, updateBuilding: BuildingUpdate): Future[Either[DatabaseError, BuildingDto]]

  def deleteBuilding(buildingId: Int, connectedBuildingId: Int): Future[Either[ServiceError, Boolean]]

  def deleteAllBuildings(): Future[Either[DatabaseError, Boolean]]

}
