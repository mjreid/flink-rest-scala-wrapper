name := "flink-rest-scala-wrapper"

lazy val commonSettings = Seq(
  organization := "com.github.mjreid",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.11"
)

lazy val root = project.in(file("."))
  .settings(commonSettings)

lazy val api = project.in(file("api"))
  .settings(commonSettings)
  .settings(name := "api")
