package com.skeleton.service.building.persistence

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import com.skeleton.service.building.BuildingModel.{Building, BuildingCreate}
import com.skeleton.service.errors.DatabaseError
import com.skeleton.service.errors.ServiceError.GenericDatabaseError
import com.skeleton.service.user.UserModel
import com.skeleton.service.user.persistence.UserPersistence
import com.skeleton.utils.database.DBAccess

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class BuildingPersistenceSQL(val dbAccess: DBAccess) extends UserPersistence {

  import dbAccess.profile.api._
  import dbAccess.{Buildings, db}

  def createBuilding(data: BuildingCreate): Future[Either[DatabaseError, Building]] =
    storeBuilding(data)

  def storeBuilding(data: BuildingCreate): Future[Either[DatabaseError, Building]] = {
    val buildingRow = Building(
      buildingId = "sdaf",
      address = data.address,
      postalArea = data.postalArea,
      country = data.country,
      geolocation = data.geolocation,
      buildingRegulation = data.buildingRegulation,
      createdOn = Timestamp.from(Instant.now())
    )

    db.run(
      (Buildings returning Buildings
        .map(_.id) into ((building, newId) => building.copy(id = Some(newId)))) += buildingRow
    )
      .transformWith {
        case Success(usr) =>
          Future.successful(Right(usr))
        case Failure(_) =>
          Future.successful(Left(GenericDatabaseError))
      }

  }

  def getUsers: Future[Either[DatabaseError, List[UserModel.User]]] = ???

  def getUser(userId: UUID): Future[Either[DatabaseError, UserModel.User]] = ???

  def createUser(data: UserModel.UserCreate): Future[Either[DatabaseError, UserModel.User]] = ???

  def updateUser(userId: UUID, updateUser: UserModel.UpdateUser): Future[Either[DatabaseError, UserModel.User]] = ???

  def updateUserPartially(userId: UUID, updateUser: UserModel.UpdateUser): Future[Either[DatabaseError, UserModel.User]] = ???

  def deleteUser(userId: UUID): Future[Either[DatabaseError, Boolean]] = ???

  def loginUser(email: String, password: String): Future[Either[DatabaseError, UserModel.User]] = ???

  def deleteAllUsers(): Future[Either[DatabaseError, Boolean]] = ???
}
