name := "virgin-cloud"

version := "0.1"

scalaVersion := "2.12.6"

lazy val akkaVersionLow = "2.5.13"
lazy val akkaVersionHigh = "10.1.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersionLow,
  "com.typesafe.akka" %% "akka-http" % akkaVersionHigh,
  "com.typesafe.akka" %% "akka-stream" % akkaVersionLow,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaVersionHigh
)