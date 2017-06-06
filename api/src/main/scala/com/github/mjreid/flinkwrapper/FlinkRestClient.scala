package com.github.mjreid.flinkwrapper

import java.io.File
import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.json._
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient
import play.shaded.ahc.org.asynchttpclient.request.body.multipart.FilePart

import scala.compat.java8.FutureConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
  * FlinkRestClient is the primary contact point for the Flink REST server.
  *
  * While this class can be invoked manually with a custom client configuration, it's recommended to use one of the
  * factory methods on the FlinkRestClient object to create a client instance.
  */
class FlinkRestClient(flinkRestClientConfig: FlinkRestClientConfig) extends AutoCloseable {

  implicit private val system = flinkRestClientConfig.maybeActorSystem.getOrElse(ActorSystem())
  implicit private val materializer = ActorMaterializer()
  private val wsClient = StandaloneAhcWSClient()
  // Append a trailing slash if not present
  private val url = if (flinkRestClientConfig.url.endsWith("/")) flinkRestClientConfig.url else flinkRestClientConfig.url + "/"

  def getConfig()(implicit ec: ExecutionContext): Future[FlinkConfigInfo] = {
    wsClient.url(url + "config").get().map { response =>
      val json: JsValue = Json.parse(response.body)
      json.validate[FlinkConfigInfo] match {
        case JsSuccess(flinkConfigInfo, _) => flinkConfigInfo
        case JsError(_) => throw new RuntimeException
      }
    }
  }

  def getJobsList()(implicit ec: ExecutionContext): Future[JobsList] = {
    wsClient.url(url + "jobs").get().map { response =>
      val json: JsValue = Json.parse(response.body)
      json.validate[JobsList] match {
        case JsSuccess(jobsList, _) => jobsList
        case JsError(_) => throw new RuntimeException
      }
    }
  }

  def getJobOverview()(implicit ec: ExecutionContext): Future[JobOverview] = {
    wsClient.url(url + "joboverview").get().map { response =>
      val json: JsValue = Json.parse(response.body)
      json.validate[JobOverview] match {
        case JsSuccess(jobOverview, _) => jobOverview
        case JsError(_) => throw new RuntimeException
      }
    }
  }

  def runProgram(
    jarId: String,
    programArguments: Option[Seq[String]] = None,
    mainClass: Option[String] = None,
    parallelism: Option[Int] = None,
    savepointPath: Option[String] = None,
    allowNonRestoredState: Option[Boolean] = None
  )(implicit ec: ExecutionContext): Future[RunProgramResult] = {
    val queryParameters: Seq[(String, String)] = Seq[Option[(String, String)]](
      programArguments.map { args => ("program-args", args.mkString(" ")) },
      mainClass.map { mc => ("entry-class", mc) },
      parallelism.map { p => ("parallelism", p.toString) },
      savepointPath.map { path => ("savepointPath", path) },
      allowNonRestoredState.map { allow => ("allowNonRestoredState", allow.toString.toLowerCase)}
    ).flatten

    wsClient.url(url + s"jars/$jarId/run").addQueryStringParameter(queryParameters:_*)
      .post("").map { response =>
      val json: JsValue = Json.parse(response.body)
      json.validate[RunProgramResult] match {
        case JsSuccess(runProgramResult, _) => runProgramResult
        case JsError(e) => throw new RuntimeException(e.toString)
      }
    }
  }

  def uploadJar(
    file: File
  )(implicit ec: ExecutionContext): Future[UploadJarResult] = {
    // This is a mess and resorts to manually using the underlying async http client (rather than play-ws) because
    // of lack of support for multipart form uploads. See https://github.com/playframework/play-ws/issues/84
    val filePart =
      new FilePart("jarfile", file, "application/x-java-archive", StandardCharsets.UTF_8, file.getName)
    val underlyingClient = wsClient.underlying[AsyncHttpClient]
    val request = underlyingClient
      .preparePost(url + "jars/upload")
      .addBodyPart(filePart)
    request.execute().toCompletableFuture.toScala.map { response =>
      val json = Json.parse(response.getResponseBody)
      json.validate[UploadJarResult] match {
        case JsSuccess(uploadJarResult, _) => uploadJarResult
        case JsError(e) => throw new RuntimeException(e.toString)
      }
    }
  }

  def getJobDetails(
    jobId: String
  )(implicit ec: ExecutionContext): Future[Job] = {
    wsClient.url(url + s"jobs/$jobId").get().map { response =>
      val json: JsValue = Json.parse(response.body)
      json.validate[Job] match {
        case JsSuccess(job, _) => job
        case JsError(e) => throw new RuntimeException(e.mkString(";"))
      }
    }
  }

  def close(): Unit = {
    system.terminate()
    wsClient.close()
  }
}

object FlinkRestClient {
  def apply(url: String): FlinkRestClient = {
    new FlinkRestClient(FlinkRestClientConfig(
      url
    ))
  }
}

case class FlinkRestClientConfig(
  url: String,
  maybeActorSystem: Option[ActorSystem] = None
)