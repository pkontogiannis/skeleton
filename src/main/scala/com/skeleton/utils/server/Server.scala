package com.skeleton.utils.server

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

trait Server {
  implicit val system: ActorSystem = ActorSystem("AkkaHttpSkeleton")
  implicit val executionContext: ExecutionContext = system.dispatcher
}
