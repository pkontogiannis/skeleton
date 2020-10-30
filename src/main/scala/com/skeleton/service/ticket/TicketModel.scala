package com.skeleton.service.ticket

import java.sql.Timestamp

object TicketModel {

  case class Ticket(
                     id: Option[Int] = None,
                     creatorId: Int,
                     title: String,
                     description: String,
                     assignee: Int,
                     buildingId: Int,
                     created_on: Timestamp
                   )

}
