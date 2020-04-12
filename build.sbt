name := "Skeleton"

version := "0.1"

scalaVersion := "2.13.1"

lazy val akkaHttpVersion        = "10.1.11"
lazy val akkaVersion            = "2.6.4"
lazy val scalaTestVersion       = "3.1.1"
lazy val argonautVersion        = "6.2.5"
lazy val slickVersion           = "3.3.2"
lazy val flywayVersion          = "6.3.3"
lazy val jwtVersion             = "4.3.0"
lazy val circeVersion           = "0.13.0"
lazy val circeExtra             = "1.31.0"
lazy val h2Version              = "1.4.200"
lazy val catsVersion            = "2.1.1"
lazy val scalaCheck             = "1.14.3"
lazy val postgresVersion        = "42.2.12"
lazy val logbackClassicVersion  = "1.2.3"
lazy val scalaLoggingVersion    = "3.9.2"
lazy val logbackEncoderVersion  = "6.3"
lazy val janinoVersion          = "3.1.2"
lazy val kamonBundleVersion     = "2.1.0"
lazy val kamonPrometheusVersion = "2.1.0"
lazy val kamonJaegerVersion     = "2.1.0"

lazy val akkaHttpSwaggerVersion  = "2.0.4"
lazy val akkaScalaSwaggerVersion = "2.0.6"
lazy val swaggerVersion          = "2.1.2"
lazy val jaxRSVersion            = "2.1.1"
lazy val akkaHttpCorsVersion     = "0.4.2"
lazy val swaggerUiVersion        = "1.4.0"

scalacOptions += "-deprecation"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-http"           % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream"         % akkaVersion,
    "com.typesafe.akka" %% "akka-http-testkit"   % akkaHttpVersion % "it,test",
    "com.typesafe.akka" %% "akka-testkit"        % akkaVersion % "it,test",
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "it,test",
    // Scala Test
    "org.scalatest"  %% "scalatest"  % scalaTestVersion % "it,test",
    "org.scalacheck" %% "scalacheck" % scalaCheck,
    // JSON Serialization Library
    "io.circe"          %% "circe-core"      % circeVersion,
    "io.circe"          %% "circe-generic"   % circeVersion,
    "io.circe"          %% "circe-parser"    % circeVersion,
    "de.heikoseeberger" %% "akka-http-circe" % circeExtra,
    // Migration of SQL Databases
    "org.flywaydb" % "flyway-core" % flywayVersion,
    // ORM
    "com.typesafe.slick" %% "slick"          % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
    "org.postgresql"     % "postgresql"      % postgresVersion,
    // Logging dependencies
    "com.typesafe.scala-logging" %% "scala-logging"           % scalaLoggingVersion,
    "ch.qos.logback"             % "logback-classic"          % logbackClassicVersion,
    "ch.qos.logback"             % "logback-access"           % logbackClassicVersion,
    "net.logstash.logback"       % "logstash-logback-encoder" % logbackEncoderVersion,
    "org.codehaus.janino"        % "janino"                   % janinoVersion,
    //JWT Dependencies
    "com.pauldijou" %% "jwt-core"  % jwtVersion,
    "com.pauldijou" %% "jwt-circe" % jwtVersion,
    "org.typelevel" %% "cats-core" % catsVersion,
    // monitoring
    "io.kamon" %% "kamon-bundle"     % kamonBundleVersion,
    "io.kamon" %% "kamon-prometheus" % kamonPrometheusVersion,
    "io.kamon" %% "kamon-jaeger"     % kamonJaegerVersion,
    // Swagger dependencies
    "ch.megard"                    %% "akka-http-cors"       % akkaHttpCorsVersion,
    "javax.ws.rs"                  % "javax.ws.rs-api"       % jaxRSVersion,
    "com.github.swagger-akka-http" %% "swagger-akka-http"    % akkaHttpSwaggerVersion,
    "com.github.swagger-akka-http" %% "swagger-scala-module" % akkaScalaSwaggerVersion,
    "io.swagger.core.v3"           % "swagger-core"          % swaggerVersion,
    "io.swagger.core.v3"           % "swagger-annotations"   % swaggerVersion,
    "io.swagger.core.v3"           % "swagger-models"        % swaggerVersion,
    "io.swagger.core.v3"           % "swagger-jaxrs2"        % swaggerVersion,
    "co.pragmati"                  %% "swagger-ui-akka-http" % swaggerUiVersion
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

mainClass in (Compile, run) := Some("com.skeleton.Main")

// *****************************************************************************
// Aliases
// *****************************************************************************

// SBT aliases to run multiple commands in a single call
//   Optionally add it:scalastyle if the project has integration tests
addCommandAlias(
  "styleCheck",
  "; scalafmtCheck ; scalastyle ; it:scalastyle"
)

// Run tests with coverage, optionally add 'it:test' if the project has
// integration tests
addCommandAlias(
  "testCoverage",
  "; coverage ; it:test ; coverageReport"
)

// Alias to run all SBT commands that are connected with quality assurance
addCommandAlias(
  "qa",
  "; styleCheck ; dependencyUpdates ; testCoverage"
)
