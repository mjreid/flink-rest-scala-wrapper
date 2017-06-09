package com.github.mjreid.flinkwrapper

import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.json._
import play.api.libs.ws.ahc.{StandaloneAhcWSClient, StandaloneAhcWSResponse}
import play.shaded.ahc.org.asynchttpclient.{AsyncCompletionHandler, AsyncHttpClient, Response => AHCResponse}
import play.shaded.ahc.org.asynchttpclient.request.body.multipart.FilePart

import scala.concurrent.{ExecutionContext, Future, Promise}

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
        case JsError(e) => throw new RuntimeException(e.mkString(";"))
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
    // Yes, these need to be query parameters rather than form values.
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
    // And on top of that, calling this method causes a dangling thread for smaller JAR files due to a bug in Netty;
    // see the sordid history at https://github.com/AsyncHttpClient/async-http-client/issues/233
    val filePart =
      new FilePart("jarfile", file, "application/x-java-archive", StandardCharsets.UTF_8, file.getName)
    val underlyingClient = wsClient.underlying[AsyncHttpClient]
    val requestBuilder = underlyingClient
      .preparePost(url + "jars/upload")
      .addBodyPart(filePart)

    val result = Promise[StandaloneAhcWSResponse]()
    val handler = new AsyncCompletionHandler[AHCResponse]() {
      override def onCompleted(response: AHCResponse): AHCResponse = {
        result.success(StandaloneAhcWSResponse(response))
        response
      }

      override def onThrowable(t: Throwable): Unit = {
        result.failure(t)
      }
    }

    underlyingClient.executeRequest(requestBuilder.build(), handler)
    val resultF = result.future

    resultF.map { response =>
      val json = Json.parse(response.body)
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

  def getJobPlan(
    jobId: String
  )(implicit ec: ExecutionContext): Future[JobPlan] = {
    wsClient.url(url + s"jobs/$jobId/plan").get().map { response =>
      val json: JsValue = Json.parse(response.body)
      json.validate[JobPlan] match {
        case JsSuccess(jobPlan, _) => jobPlan
        case JsError(e) => throw new RuntimeException(e.mkString(";"))
      }
    }
  }

  def cancelJob(jobId: String)(implicit ec: ExecutionContext): Future[Unit] = {
    wsClient.url(url + s"jobs/$jobId/cancel").delete().map { _ => ()}
  }

  def cancelJobWithSavepoint(
    jobId: String,
    targetDirectory: Option[String] = None
  )(implicit ec: ExecutionContext): Future[CancelJobAccepted] = {
    val targetDirectoryUrl = targetDirectory
      .map { unencoded => URLEncoder.encode(unencoded, "UTF-8")}
      .map { d => s"target-directory/$d/" }.getOrElse("")
    val fullUrl = url + s"jobs/$jobId/cancel-with-savepoint/$targetDirectoryUrl"

    wsClient.url(fullUrl).get().map { response =>
      val json: JsValue = Json.parse(response.body)
      json.validate[CancelJobAccepted] match {
        case JsSuccess(cancelJobRequest, _) => cancelJobRequest
        case JsError(e) => throw new RuntimeException(e.mkString(";"))
      }
    }
  }

  def getCancellationStatus(
    location: String
  )(implicit ec: ExecutionContext): Future[CancellationStatusInfo] = {
    wsClient.url(url + location).get().map { response =>
      val json: JsValue = Json.parse(response.body)
      json.validate[CancellationStatusInfo] match {
        case JsSuccess(info, _) => info
        case JsError(e) => throw new RuntimeException(e.mkString(";"))
      }
    }
  }

  def getJobExceptions(
    jobId: String
  )(implicit ec: ExecutionContext): Future[JobExceptions] = {
    wsClient.url(url + s"jobs/$jobId/exceptions/").get().map { response =>
      val json: JsValue = Json.parse(response.body)
      json.validate[JobExceptions] match {
        case JsSuccess(jobExceptions, _) => jobExceptions
        case JsError(e) => throw new RuntimeException(e.mkString(";"))
      }
    }
  }

  def close(): Unit = {
    wsClient.close()
    system.terminate()
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