ThisBuild / version := "0.0.1"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file(".")).settings(
  libraryDependencies ++= Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core"               % "1.9.4",
    "com.softwaremill.sttp.tapir" %% "tapir-armeria-server-zio" % "1.9.4",
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"  % "1.9.4",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % "1.9.4"
  )
)
