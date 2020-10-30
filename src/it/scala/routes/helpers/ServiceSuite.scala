package routes.helpers

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.skeleton.service.{Dependencies, Routes}
import com.skeleton.utils.database.Migration
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.matching.Regex

abstract class ServiceSuite
    extends AnyWordSpec
    with ScalatestRouteTest
    with Matchers
    with Routes
    with BeforeAndAfterAll
    with BeforeAndAfterEach
    with Migration {

  implicit val actorSystem: ActorSystem =
    ActorSystem("test-actor-system")

  val dependencies: Dependencies = Dependencies.fromConfig

  override def beforeAll(): Unit =
//    Await.result(userPersistence.deleteDatabaseContent, 10.seconds)
    flywayMigrate()

  override def afterAll(): Unit = {
    dependencies.dbAccess.db.close()
    Await.result(actorSystem.terminate(), 10.seconds)
  }

  private def unsafeExtractHostPort(string: String): (String, Int) = {
    val HostnameRegEx: Regex = "(.+):(\\d{4,5})".r
    string match {
      case HostnameRegEx(host, port) => (host, port.toInt)
      case _ => throw new Exception(s"Failed to parse hostname: $string")
    }
  }

}
