package com.skeleton.utils.server

import com.typesafe.config.{ Config => TSConfig }

case class DatabaseConfig(driver: String, url: String, username: String, password: String, profile: String)

object DatabaseConfig {

  def apply(config: TSConfig): DatabaseConfig =
    DatabaseConfig(
      config.getString("database.driver"),
      "jdbc:postgresql://" + config.getString("database.properties.serverName") + ":" +
      config.getString("database.properties.portNumber") + "/" +
      config.getString("database.properties.databaseName"),
      //      config.getString("database.url"),
      config.getString("database.properties.user"),
      config.getString("database.properties.password"),
      config.getString("database.profile")
    )

}
