package com.skeleton.service.building.persistence

import com.skeleton.service.building.BuildingModel.{Building, BuildingCreate, BuildingUpdate}
import com.skeleton.service.errors.DatabaseError
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

trait BuildingPersistence extends LazyLogging {

  def getBuildings: Future[Either[DatabaseError, List[Building]]]

  def getBuilding(buildingId: Int): Future[Either[DatabaseError, Building]]

  def createBuilding(data: BuildingCreate): Future[Either[DatabaseError, Building]]

  def updateBuilding(buildingId: Int, data: BuildingUpdate): Future[Either[DatabaseError, Building]]

  def updateBuildingPartially(buildingId: Int, data: BuildingUpdate): Future[Either[DatabaseError, Building]]

  def deleteBuilding(buildingId: Int): Future[Either[DatabaseError, Boolean]]

  def deleteAllBuildings(): Future[Either[DatabaseError, Boolean]]

}
