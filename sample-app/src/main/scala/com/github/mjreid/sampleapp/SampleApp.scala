package com.github.mjreid.sampleapp

import java.io.File
import java.util.concurrent.{ExecutorService, Executors, ThreadFactory, TimeUnit}

import com.github.mjreid.flinkwrapper.{CancelJobAccepted, CancellationStatusInfo, FlinkRestClient, RunProgramResult}

import scala.concurrent.{Await, ExecutionContext}
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

  def runStartProgram(jarName: String, mainClass: Option[String]): RunProgramResult = {
    val result = flinkClient.runProgram(
      jarName,//"c5556a8b-ea02-4c69-b7a0-59011cd7e4bd_bs.jar",
      mainClass = mainClass.orElse(Some("org.example.WordCount"))
    )

    val jobResult = Await.result(result, FiniteDuration(1, TimeUnit.SECONDS))
    println(jobResult)
    jobResult
  }

  def runUploadJar(): String = {
    val flinkUrl = "http://localhost:8081"
    val flinkClient = FlinkRestClient(flinkUrl)
    val resultF = flinkClient.uploadJar(
      new File("/tmp/bs2.jar")
    )

    val result = Await.result(resultF, FiniteDuration(4, TimeUnit.SECONDS))
    println(result)
    result.filename
  }

  def runGetJobDetails(jobId: String): Unit = {
    // val jobId = "67fef029dd746f4c47cf61d28189a4fd"

    val result = flinkClient.getJobDetails(jobId)

    val response = Await.result(result, FiniteDuration(1, TimeUnit.SECONDS))
    println(response)
  }

  def runGetJobPlan(jobId: String): Unit = {
    val result = flinkClient.getJobPlan(jobId)
    val response = Await.result(result, FiniteDuration(1, TimeUnit.SECONDS))
    println(response)
  }

  def runCancelJob(jobId: String): Unit = {
    val resultF = flinkClient.cancelJob(jobId)
    val result = Await.result(resultF, FiniteDuration(1, TimeUnit.SECONDS))
    println(result)
  }

  def runCancelJobWithSavepoint(jobId: String, savepointPath: String): CancelJobAccepted = {
    val resultF = flinkClient.cancelJobWithSavepoint(jobId, Some(savepointPath))
    val result = Await.result(resultF, FiniteDuration(1, TimeUnit.SECONDS))
    println(result)
    result
  }

  def runGetCancellationStatus(location: String): CancellationStatusInfo = {
    ???
  }

  runGetConfig()
  runGetJobsList()
  runGetJobOverview()
  runGetJobOverview()
  val jarName = runUploadJar()
  val runProgramResult = runStartProgram(jarName, None)
  runGetJobDetails(runProgramResult.jobId)
  runGetJobPlan(runProgramResult.jobId)

  {
    // Streaming and cancellation testing
    val kafkaProgramResult = runStartProgram(jarName, Some("org.example.KafkaEcho"))
    Thread.sleep(1000)
    runGetJobDetails(kafkaProgramResult.jobId)
    runCancelJob(kafkaProgramResult.jobId)
    runGetJobDetails(kafkaProgramResult.jobId)
  }

  {
    // Streaming and cancellation with savepoint testing
    val kafkaProgramResult = runStartProgram(jarName, Some("org.example.KafkaEcho"))
    Thread.sleep(1000)
    runGetJobDetails(kafkaProgramResult.jobId)
    runCancelJobWithSavepoint(kafkaProgramResult.jobId, "/tmp")
    runGetJobDetails(kafkaProgramResult.jobId)
  }

  flinkClient.close()
}
