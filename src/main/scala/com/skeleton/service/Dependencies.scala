package com.skeleton.service

import akka.actor.ActorSystem
import com.skeleton.utils.config.Configuration
import com.typesafe.config.{Config, ConfigFactory}

case class Dependencies()


object Dependencies {
  private val config: Config = ConfigFactory.load()


  def fromConfig(configuration: Configuration)(implicit system: ActorSystem): Dependencies = {


    Dependencies()
  }
}