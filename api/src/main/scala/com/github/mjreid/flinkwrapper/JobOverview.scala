package com.github.mjreid.flinkwrapper

import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.concurrent.TimeUnit

import com.github.mjreid.flinkwrapper.util.Readers
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.duration.{Duration, FiniteDuration}

/**
  * JobOverview contains a summary of all jobs, grouped by status.
  */
case class JobOverview(
  running: Seq[JobSummary],
  finished: Seq[JobSummary]
)

object JobOverview {
  implicit val reads: Reads[JobOverview] = (
    (JsPath \ "running").read[Seq[JobSummary]] and
      (JsPath \ "finished").read[Seq[JobSummary]]
  )(JobOverview.apply _)
}

case class JobSummary(
  id: String,
  name: String,
  state: JobStatus,
  startTime: LocalDateTime,
  endTime: LocalDateTime,
  duration: Duration,
  lastModification: LocalDateTime,
  taskCounts: TaskCounts
)

object JobSummary {
  implicit val reads: Reads[JobSummary] = (
    (JsPath \ "jid").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "state").read[JobStatus] and
      (JsPath \ "start-time").read[LocalDateTime](Readers.millisLocalDateTimeReader) and
      (JsPath \ "end-time").read[LocalDateTime](Readers.millisLocalDateTimeReader) and
      (JsPath \ "duration").read[Duration](Readers.millisDurationReader) and
      (JsPath \ "last-modification").read[LocalDateTime](Readers.millisLocalDateTimeReader) and
      (JsPath \ "tasks").read[TaskCounts]
  )(JobSummary.apply _)
}

case class TaskCounts(
  total: Int,
  pending: Int,
  running: Int,
  finished: Int,
  canceling: Int,
  canceled: Int,
  failed: Int
)

object TaskCounts {
  implicit val reads: Reads[TaskCounts] = (
    (JsPath \ "total").read[Int] and
      (JsPath \ "pending").read[Int] and
      (JsPath \ "running").read[Int] and
      (JsPath \ "finished").read[Int] and
      (JsPath \ "canceling").read[Int] and
      (JsPath \ "canceled").read[Int] and
      (JsPath \ "failed").read[Int]
  )(TaskCounts.apply _)
}

sealed trait JobStatus
case object Running extends JobStatus
case object Finished extends JobStatus
case object Canceled extends JobStatus
case object Failed extends JobStatus

object JobStatus {
  implicit val reads: Reads[JobStatus] = Reads {
    case JsString(statusString) =>
      statusString.toUpperCase match {
        case "RUNNING" => JsSuccess(Running)
        case "CANCELLED" => JsSuccess(Canceled)
        case "FINISHED" => JsSuccess(Finished)
        case "FAILED" => JsSuccess(Failed)
      }
    case _ => JsError("Not a valid job status")
  }
}