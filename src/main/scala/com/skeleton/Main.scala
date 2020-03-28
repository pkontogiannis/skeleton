package com.skeleton

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.skeleton.service.{ Dependencies, Routes }
import com.skeleton.utils.database.Migration
import com.skeleton.utils.server.{ Config, Server }

import scala.concurrent.Future
import scala.util.{ Failure, Success }

object Main extends App with Server with Config with Migration {

  def startApplication(): Unit = {

    val dependencies: Dependencies = Dependencies.fromConfig(configuration)

    val routes: Route = cors()(Routes.buildRoutes(dependencies))

    val serverBinding: Future[Http.ServerBinding] =
      Http().bindAndHandle(
        routes,
        configuration.serverConfig.host,
        configuration.serverConfig.port
      )

    flywayMigrate()

    serverBinding.onComplete {
      case Success(bound) =>
        println(
          s"com.skeleton.utils.server.Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/"
        )
      case Failure(e) =>
        Console.err.println(s"com.skeleton.utils.server.Server could not start!")
        e.printStackTrace()
        system.terminate()
    }
  }

  startApplication()

}
