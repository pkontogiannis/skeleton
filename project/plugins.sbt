lazy val sbtNativePackagerVersion = "1.6.1"
lazy val sbtRevolver              = "0.9.1"

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % sbtNativePackagerVersion)
addSbtPlugin("io.spray"         % "sbt-revolver"        % sbtRevolver)
