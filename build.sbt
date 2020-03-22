
name := "Skeleton"

version := "0.1"

scalaVersion := "2.13.1"

version := "0.1"

scalaVersion := "2.12.8"

lazy val akkaHttpVersion = "10.1.11"
lazy val akkaVersion = "2.6.4"
lazy val scalaTestVersion = "3.1.1"
lazy val argonautVersion = "6.2.5"
lazy val slickVersion = "3.3.2"
lazy val flywayVersion = "6.3.1"
lazy val jwtVersion = "4.3.0"
lazy val circeVersion = "0.13.0"
lazy val circeExtra = "1.31.0"
lazy val h2Version = "1.4.200"
lazy val catsVersion = "2.1.1"
lazy val scalaCheck = "1.14.3"
lazy val neoTypesVersion = "0.7.0"
lazy val neo4jVersion = "4.0.1"
lazy val neo4jDriverVersion = "4.0.1"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,

    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "it,test",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "it,test",
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "it,test",

    // Scala Test
    "org.scalatest" %% "scalatest" % scalaTestVersion % "it,test",
    "org.scalacheck" %% "scalacheck" % scalaCheck,

    // JSON Serialization Library
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "de.heikoseeberger" %% "akka-http-circe" % circeExtra,

    // Migration of SQL Databases
    "org.flywaydb" % "flyway-core" % flywayVersion,

    // ORM
    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.h2database" % "h2" % h2Version, // % Test,

    // Logging dependencies
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",

    "com.pauldijou" %% "jwt-core" % jwtVersion,
    "com.pauldijou" %% "jwt-circe" % jwtVersion,

    "org.typelevel" %% "cats-core" % catsVersion,

    "org.neo4j.driver" % "neo4j-java-driver" % neo4jDriverVersion,
    "org.neo4j" % "neo4j-graphdb-api" % neo4jVersion,
    "org.neo4j" % "neo4j-kernel" % neo4jVersion,
    "org.neo4j" % "neo4j-ogm-embedded-driver" % "3.2.10"
  )
}


lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    resolvers += Resolver.bintrayRepo("unisay", "maven"),
    Defaults.itSettings,
    parallelExecution in ThisBuild := false
  )


enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

mainClass in(Compile, run) := Some("com.skeleton.Main")
