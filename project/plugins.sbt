lazy val sbtNativePackagerVersion = "1.6.1"
lazy val sbtRevolverVersion       = "0.9.1"
lazy val sbtScalafmtVersion       = "2.3.2"
lazy val sbtUpdatesVersion        = "0.5.0"
lazy val sbtScoverageVersion      = "1.6.1"
lazy val scalaStyleVersion        = "1.0.0"

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager"    % sbtNativePackagerVersion)
addSbtPlugin("io.spray"         % "sbt-revolver"           % sbtRevolverVersion)
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"           % sbtScalafmtVersion)
addSbtPlugin("com.timushev.sbt" % "sbt-updates"            % sbtUpdatesVersion)
addSbtPlugin("org.scoverage"    % "sbt-scoverage"          % sbtScoverageVersion)
addSbtPlugin("org.scalastyle"   %% "scalastyle-sbt-plugin" % scalaStyleVersion)
