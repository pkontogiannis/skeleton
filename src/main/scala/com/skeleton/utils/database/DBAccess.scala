package com.skeleton.utils.database

import akka.actor.ActorSystem
import com.skeleton.persistence.Schema
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class DBAccess(actorSystem: ActorSystem) extends Schema {

  //  val db: Database = Database.forConfig("h2mem")
  val db: Database = Database.forConfig("database")

  private val tables = List(Users)

  private val existing: Future[Vector[MTable]] = db.run(MTable.getTables)
  private val f = existing.flatMap(v => {
    val names = v.map(mt => mt.name.name)
    val createIfNotExist = tables.filter(table =>
      !names.contains(table.baseTableRow.tableName)).map(_.schema.create)
    db.run(DBIO.sequence(createIfNotExist))
  })
  Await.result(f, Duration.Inf)

}
