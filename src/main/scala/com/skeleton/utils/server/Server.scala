package com.skeleton.utils.server

import akka.actor.ActorSystem
import kamon.Kamon

import scala.concurrent.ExecutionContext

trait Server {
  Kamon.init()
  implicit val system: ActorSystem                = ActorSystem("AkkaHttpSkeleton")
  implicit val executionContext: ExecutionContext = system.dispatcher
}
