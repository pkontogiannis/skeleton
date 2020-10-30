package com.skeleton.service.health

import com.skeleton.service.health.HealthModel.HealthStatus
import com.skeleton.utils.database.DBAccess
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class HealthServiceDefault(val dbAccess: DBAccess) extends HealthService {

  private lazy val maxWait = 100 milliseconds

  def ready: HealthStatus = {
    val q = sql"SELECT 1".as[Int]
    val f = dbAccess.db
      .run(q.asTry)
      .map {
        case Success(_) => HealthStatus.OK
        case Failure(_) => HealthStatus.DOWN
      }

    Try(Await.result(f, maxWait)) match {
      case Success(healthStatus) => healthStatus
      case Failure(_) => HealthStatus.DOWN
    }
  }

}
