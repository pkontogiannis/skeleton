package com.skeleton.persistence.tables

import java.sql.Timestamp

import com.skeleton.persistence.{Schema, SlickJdbcProfile}
import com.skeleton.service.ticket.TicketModel.Ticket
import com.skeleton.service.user.UserModel
import slick.lifted.{ForeignKeyQuery, ProvenShape}

trait TicketTableDef extends Schema {

  self: SlickJdbcProfile =>

  import profile.api._

  class TicketTable(tag: Tag) extends Table[Ticket](tag, Some("skeleton"), "ticket") {

    def * : ProvenShape[Ticket] =
      (
        id.?,
        creatorId,
        title,
        description,
        assignee,
        buildingId, // what if it's related with the appartment
        createdOn
      ) <> ((Ticket.apply _).tupled, Ticket.unapply)

    def id: Rep[Int] = column[Int]("building_id", O.PrimaryKey, O.AutoInc)

    def creatorId: Rep[Int] = column[Int]("creator_id")

    def title: Rep[String] = column[String]("title")

    def description: Rep[String] = column[String]("description")

    def assignee: Rep[Int] = column[Int]("assignee")

    def buildingId: Rep[Int] = column[Int]("building_id")

    def createdOn: Rep[Timestamp] = column[Timestamp]("created_on")

    def creator: ForeignKeyQuery[UserTable, UserModel.User] = foreignKey("creator_fk", creatorId, Users)(_.id, onUpdate =
      ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  }

}
