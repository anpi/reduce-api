name := "reduce-api"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.31.0",
  "com.github.finagle" %% "finch-circe" % "0.31.0",
  "io.circe" %% "circe-generic" % "0.12.2",
  "io.circe" %% "circe-core" % "0.12.2",
  "io.circe" %% "circe-parser" % "0.12.2",
  "io.circe" %% "circe-generic-extras" % "0.12.2",
  "com.softwaremill.sttp.client" %% "core" % "2.0.0-RC1"
)
