import sbt.Keys.libraryDependencies

name := "BaseInfrastructure"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.8.0",
  "io.circe" %% "circe-generic"% "0.8.0",
  "io.circe" %% "circe-parser" % "0.8.0",
  "net.codingwell" %% "scala-guice" % "4.1.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "com.typesafe" % "config" % "1.3.1",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test")