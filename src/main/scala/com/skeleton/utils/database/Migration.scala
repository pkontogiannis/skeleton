package com.skeleton.utils.database

import com.skeleton.utils.server.Config
import com.typesafe.scalalogging.LazyLogging
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.internal.jdbc.DriverDataSource

trait Migration extends Config with LazyLogging {

  val dataSource = new DriverDataSource(
    Thread.currentThread.getContextClassLoader,
    dbConfig.driver,
    dbConfig.url,
    dbConfig.username,
    dbConfig.password
  )

  val flywayConfig: FluentConfiguration = Flyway
    .configure()
    .dataSource(dataSource)
    .schemas("flyway_migrations", "extensions", "skeleton")

  val flyway: Flyway = new Flyway(flywayConfig)

  def flywayMigrate(): Int = {
    logger.info(s"[${this.getClass.getSimpleName}] successfully made the migration.")
    flyway.migrate()
  }

  def reloadSchema(): Int = {
    flyway.clean()
    flyway.migrate()
  }

}
