package com.skeleton.service.user.persistence

import java.util.UUID

import com.skeleton.service.errors.DatabaseError
import com.skeleton.service.errors.ServiceError.{ GenericDatabaseError, RecordAlreadyExists, RecordNotFound }
import com.skeleton.service.user.UserModel
import com.skeleton.service.user.UserModel.{ UpdateUser, User, UserCreate }
import com.skeleton.utils.database.DBAccess

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

class UserPersistenceSQL(val dbAccess: DBAccess) extends UserPersistence {

  import dbAccess._
  import dbAccess.profile.api._

  private type UserSelection = Query[UserTable, User, Seq]

  def getUsers: Future[Either[DatabaseError, List[User]]] =
    db.run(Users.result).transformWith {
      case Success(users) => Future.successful(Right(users.toList))
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }

  def createUser(data: UserCreate): Future[Either[DatabaseError, User]] =
    exists(data.email).flatMap {
      case true =>
        Future.successful(Left(RecordAlreadyExists))
      case false =>
        storeUser(data)
    }

  def storeUser(data: UserCreate): Future[Either[DatabaseError, User]] = {
    val userRow = User(
      userId    = UUID.randomUUID(),
      email     = data.email,
      password  = data.password,
      firstName = data.firstName,
      lastName  = data.lastName,
      role      = data.role
    )

    db.run(
        (Users returning Users
          .map(_.id) into ((user, newId) => user.copy(id = Some(newId)))) += userRow
      )
      .transformWith {
        case Success(usr) =>
          Future.successful(Right(usr))
        case Failure(_) =>
          Future.successful(Left(GenericDatabaseError))
      }

  }

  def exists(email: String): Future[Boolean] = db.run {
    Users.filter(_.email === email).exists.result
  }

  def getUser(userId: UUID): Future[Either[DatabaseError, User]] =
    db.run(Users.filter(_.userId === userId).result.headOption).transformWith {
      case Success(optUser) =>
        optUser match {
          case Some(user) => Future.successful(Right(user))
          case None => Future.successful(Left(RecordNotFound))
        }
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }

  def loginUser(email: String, password: String): Future[Either[DatabaseError, User]] =
    db.run(Users.filter(user => user.email === email && user.password === password).result.headOption)
      .transformWith {
        case Success(optUser) =>
          optUser match {
            case Some(user) => Future.successful(Right(user))
            case None => Future.successful(Left(RecordNotFound))
          }
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }

  def updateUser(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, User]] =
    getUser(userId).flatMap {
      case Right(user) =>
        updateUser.email match {
          case Some(email) if user.email != email =>
            exists(email).flatMap {
              case true =>
                Future.successful(Left(RecordAlreadyExists))
              case false =>
                updateUserWithEmail(userId, updateUser)
            }
          case Some(_) => updateUserWithEmail(userId, updateUser)
          case None => updateUserWithEmail(userId, updateUser)
        }
      case Left(_) => Future.successful(Left(GenericDatabaseError))
    }

  def updateUserWithEmail(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, User]] = {
    val updatedUser: User = UserModel.updateUserToUser(userId, updateUser)
    val actions = for {
      userOpt <- getUserOf(userId).result.headOption
      updateActionOption = userOpt.map(_ => getUserOf(userId).update(updatedUser.copy(id = userOpt.get.id)))
      _ <- updateActionOption.getOrElse(DBIO.successful(0))
      us <- getUserOf(userId).result.headOption
    } yield us

    db.run(actions.transactionally).transformWith {
      case Success(optUser) =>
        optUser match {
          case Some(user) => Future.successful(Right(user))
          case None => Future.successful(Left(RecordNotFound))
        }
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }
  }

  def updateUserPartially(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, User]] =
    getUser(userId).flatMap {
      case Right(user) =>
        updateUser.email match {
          case Some(email) if user.email != email =>
            exists(email).flatMap {
              case true =>
                Future.successful(Left(RecordAlreadyExists))
              case false =>
                updateUserPartiallyWithEmail(userId, updateUser)
            }
          case Some(_) => updateUserPartiallyWithEmail(userId, updateUser)
          case None => updateUserPartiallyWithEmail(userId, updateUser)
        }
      case Left(_) => Future.successful(Left(GenericDatabaseError))
    }

  //    updateUser.email match {
  //      case Some(email) =>
  //        exists(email).flatMap {
  //          case true =>
  //            Future.successful(Left(RecordAlreadyExists))
  //          case false => updateUserPartiallyWithEmail(userId, updateUser)
  //        }
  //      case None => updateUserPartiallyWithEmail(userId, updateUser)
  //    }

  def updateUserPartiallyWithEmail(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, User]] = {
    val actions = for {
      userOpt <- getUserOf(userId).result.headOption
      updateActionOption = userOpt.map { oldUser =>
        val updatedUser = UserModel.updateUserRow(oldUser, updateUser)
        getUserOf(userId).update(updatedUser)
      }
      _ <- updateActionOption.getOrElse(DBIO.successful(0))
      us <- getUserOf(userId).result.headOption
    } yield us
    db.run(actions.transactionally).transformWith {
      case Success(optUser) =>
        optUser match {
          case Some(user) => Future.successful(Right(user))
          case None => Future.successful(Left(RecordNotFound))
        }
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }
  }

  def editUser(userId: UUID, updateUser: UpdateUser): Future[User] = {
    val updatedUser = UserModel.updateUserToUser(userId, updateUser)
    val actions = for {
      userOpt <- getUserOf(userId).result.headOption
      updateActionOption = userOpt.map(_ => getUserOf(userId).update(updatedUser.copy(id = userOpt.get.id)))
      _ <- updateActionOption.getOrElse(DBIO.successful(0))
      us <- getUserOf(userId).result.head
    } yield us
    db.run(actions.transactionally)
  }

  def deleteUser(userId: UUID): Future[Either[DatabaseError, Boolean]] =
    db.run(getUserOf(userId).delete).transformWith {
      case Success(res) =>
        res match {
          case 0 => Future.successful(Right(false))
          case 1 => Future.successful(Right(true))
        }
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }

  private def getUserOf(userId: UUID): UserSelection =
    Users.filter(_.userId === userId)

  def deleteAllUsers(): Future[Either[DatabaseError, Boolean]] =
    db.run(Users.delete).transformWith {
      case Success(res) =>
        res match {
          case 0 => Future.successful(Right(false))
          case 1 => Future.successful(Right(true))
        }
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }

  private def getUserQuery(userId: UUID): DBIO[Option[User]] =
    getUserOf(userId).result.headOption
}
