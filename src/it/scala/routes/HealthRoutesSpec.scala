package routes

import java.lang.management.ManagementFactory

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.skeleton.service.health.{ HealthRoutes, HealthService }
import com.zaxxer.hikari.HikariPoolMXBean
import javax.management.{ JMX, ObjectName }
import routes.helpers.ServiceSuite

class HealthRoutesSpec extends ServiceSuite {

  trait Fixture {
    lazy val poolProxy: HikariPoolMXBean = JMX.newMXBeanProxy(
      ManagementFactory.getPlatformMBeanServer,
      new ObjectName("com.zaxxer.hikari:type=Pool (database)"),
      classOf[HikariPoolMXBean]
    )

    val healthService: HealthService = dependencies.healthService
    val healthRoutes: Route          = new HealthRoutes(healthService).healthRoutes
  }

  "Health Routes" should {

    "return 200 OK on /health/live" in new Fixture {
      Get("/health/live") ~> healthRoutes ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
      }
    }

    "return 200 OK on /health/ready when connected to DB" in new Fixture {
      Get("/health/ready") ~> healthRoutes ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
      }
    }

    "return 500 DOWN on /health/ready when DB Future doesn't return after specified time" in new Fixture {
      poolProxy.suspendPool()
      Get("/health/ready") ~> healthRoutes ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.InternalServerError
      }
    }

  }

}
