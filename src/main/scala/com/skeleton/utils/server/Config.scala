package com.skeleton.utils.server

import com.skeleton.utils.config.Configuration

trait Config {

  val configuration: Configuration = Configuration.default
  val dbConfig: DatabaseConfig     = configuration.databaseConfig

}
