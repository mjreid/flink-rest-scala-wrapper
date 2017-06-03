package com.github.mjreid.sampleapp

import java.io.File
import java.util.concurrent.TimeUnit

import com.github.mjreid.flinkwrapper.FlinkRestClient

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * SampleApp is a small program that serves as a "manual integration test suite" of sorts. Hopefully temporary until a
  * real integration test solution is added.
  */
object SampleApp extends App {

  val flinkUrl = "http://localhost:8081"
  val flinkClient = FlinkRestClient(flinkUrl)

  def runGetConfig(): Unit = {
    val result = flinkClient.getConfig().map { config =>
      println(config)
    }

    Await.result(result, FiniteDuration(1, TimeUnit.SECONDS))
  }

  def runGetJobsList(): Unit = {
    val result = flinkClient.getJobsList().map { jobsList =>
      println(jobsList)
    }

    Await.result(result, FiniteDuration(1, TimeUnit.SECONDS))
  }

  def runGetJobOverview(): Unit = {
    val result = flinkClient.getJobOverview().map { jobOverview =>
      println(jobOverview)
    }

    Await.result(result, FiniteDuration(1, TimeUnit.SECONDS))
  }

  def runStartProgram(): Unit = {
    val result = flinkClient.runProgram(
      "f68c0290-8fa4-4c92-bca7-fb0770996f83_Flink Project-assembly-0.1-SNAPSHOT.jar",
      mainClass = Some("org.example.WordCount")
    ).map { jobResult =>
      println(jobResult)
    }

    Await.result(result, FiniteDuration(1, TimeUnit.SECONDS))
  }

  def runUploadJar(): Unit = {
    val flinkUrl = "http://localhost:8081"
    val flinkClient = FlinkRestClient(flinkUrl)
    val result = flinkClient.uploadJar(
      new File("/tmp/fltest.jar")
    ).map { result =>
      println(result)
    }

    Await.result(result, FiniteDuration(1, TimeUnit.SECONDS))
  }

  runGetConfig()
  runGetJobsList()
  runGetJobOverview()
  runStartProgram()
  runUploadJar()


  flinkClient.close()
}
