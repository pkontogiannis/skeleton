package com.skeleton.utils.server

import com.typesafe.config.{Config => TSConfig}

case class ServerConfig(host: String, version: String, port: Int)

object ServerConfig {

  def apply(config: TSConfig): ServerConfig =
    ServerConfig(
      config.getString("server.host"),
      config.getString("server.version"),
      config.getInt("server.port")
    )

}
