package com.skeleton.utils.database

import com.typesafe.config.{ Config, ConfigFactory }
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.internal.jdbc.DriverDataSource

object FlywayService {

  val config: Config   = ConfigFactory.load()
  val db: Config       = config.getConfig("database")
  val dbDriver: String = db.getString("driver")
  val dbProps: Config  = db.getConfig("properties")
  val dbHost: String   = dbProps.getString("serverName")
  val dbPort: String   = dbProps.getString("portNumber")
  val dbName: String   = dbProps.getString("databaseName")
  val dbUrl: String    = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName
  val dbUser: String   = dbProps.getString("user")
  val dbPass: String   = dbProps.getString("password")

  val dataSource = new DriverDataSource(Thread.currentThread.getContextClassLoader, dbDriver, dbUrl, dbUser, dbPass)

  val flywayConfig: FluentConfiguration = Flyway
    .configure()
    .dataSource(dataSource)
    .schemas("flyway_migrations", "extensions", "skeleton")

  val flyway: Flyway = new Flyway(flywayConfig)

  def migrate(): Unit = println("migrated: " + flyway.migrate())

  def clean(): Unit = flyway.clean()

  def recreate(): Unit = {
    this.clean()
    this.migrate()
  }
}
