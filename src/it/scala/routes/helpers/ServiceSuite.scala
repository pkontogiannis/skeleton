package routes.helpers

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.skeleton.service.Routes
import com.skeleton.service.user.persistence.UserPersistenceSQL
import com.skeleton.utils.database.DBAccess
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.util.matching.Regex

abstract class ServiceSuite extends AnyWordSpec with ScalatestRouteTest with Matchers
  with Routes with BeforeAndAfterAll with BeforeAndAfterEach {

  implicit val actorSystem: ActorSystem =
    ActorSystem("test-actor-system")

  val config: Config = ConfigFactory.load()
  val dbAccess: DBAccess = DBAccess(system)

  val userPersistence: UserPersistenceSQL = new UserPersistenceSQL(dbAccess)

  override def beforeAll(): Unit = {
    //    Await.result(userPersistence.deleteDatabaseContent, 10.seconds)
  }

  override def afterAll(): Unit = {
    //    driver.close()
    //    Await.result(actorSystem.terminate(), 10.seconds)
  }

  private def unsafeExtractHostPort(string: String): (String, Int) = {
    val HostnameRegEx: Regex = "(.+):(\\d{4,5})".r
    string match {
      case HostnameRegEx(host, port) => (host, port.toInt)
      case _ => throw new Exception(s"Failed to parse hostname: $string")
    }
  }

}
