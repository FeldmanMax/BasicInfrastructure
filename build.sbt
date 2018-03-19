import sbt.Keys.libraryDependencies

autoScalaLibrary := true
managedScalaInstance := false

lazy val commonSettings = Seq(
  organization := "max.feldman",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.4",
  name := "BasicInfrastructure"
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.8.0",
      "io.circe" %% "circe-generic"% "0.8.0",
      "io.circe" %% "circe-parser" % "0.8.0",
      "net.codingwell" %% "scala-guice" % "4.1.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "com.typesafe" % "config" % "1.3.1",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test")
  )

mappings in (Compile, packageBin) ~= { _.filter(!_._1.getName.contains("logback")) }

//classpathTypes in Runtime += baseDirectory.value + "/src/main/resources/"
//classpathTypes in Test += baseDirectory.value + "/src/test/resources/"


exportJars := true