import sbt._

object Dependencies {
  private val playWsStandaloneVersion = "1.0.0-RC1"
  private val playJsonVersion = "2.6.0-M6"
  private val slf4jVersion = "1.7.25"
  private val scalaTestVersion = "3.0.3"
  private val scalaMockVersion = "3.6.0"

  val playWs: ModuleID = "com.typesafe.play" %% "play-ahc-ws-standalone" % playWsStandaloneVersion
  val slf4j: ModuleID = "org.slf4j" % "slf4j-api" % slf4jVersion
  val playJson: ModuleID = "com.typesafe.play" %% "play-json" % playJsonVersion
  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  val scalaMock: ModuleID = "org.scalamock" %% "scalamock-scalatest-support" % scalaMockVersion % "test"

  val all = Seq(playWs, slf4j, playJson, scalaTest, scalaMock)
}