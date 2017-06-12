name := "flink-rest-scala-wrapper"

lazy val commonSettings = Seq(
  organization := "com.github.mjreid",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.11"
)

lazy val root = project.in(file("."))
  .settings(commonSettings)
  .aggregate(api, sampleApp)

lazy val api = project.in(file("api"))
  .settings(commonSettings)
  .settings(name := "api")
  .settings(libraryDependencies ++= Dependencies.all)

lazy val sampleApp = project.in(file("sample-app"))
  .settings(commonSettings)
  .settings(name := "sample-app")
  .dependsOn(api)