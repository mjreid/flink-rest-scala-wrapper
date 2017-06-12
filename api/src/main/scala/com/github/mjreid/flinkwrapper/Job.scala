package com.github.mjreid.flinkwrapper

import java.time.LocalDateTime

import com.github.mjreid.flinkwrapper.util.Readers
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.duration.Duration

case class Job(
  id: String,
  name: String,
  isStoppable: Boolean,
  state: JobStatus.JobStatus,
  startTime: LocalDateTime,
  endTime: LocalDateTime,
  duration: Duration,
  now: LocalDateTime,
  stateTimestamps: StateTimes,
  vertices: Seq[JobVertex],
  statusCounts: VertexTaskCounts,
  plan: JobPlan
)

object Job {
  implicit val reads: Reads[Job] = (
    (JsPath \ "jid").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "isStoppable").read[Boolean] and
      (JsPath \ "state").read[JobStatus.JobStatus] and
      (JsPath \ "start-time").read[LocalDateTime](Readers.millisLocalDateTimeReader) and
      (JsPath \ "end-time").read[LocalDateTime](Readers.millisLocalDateTimeReader) and
      (JsPath \ "duration").read[Duration](Readers.millisDurationReader) and
      (JsPath \ "now").read[LocalDateTime](Readers.millisLocalDateTimeReader) and
      (JsPath \ "timestamps").read[StateTimes] and
      (JsPath \ "vertices").read[Seq[JobVertex]] and
      (JsPath \ "status-counts").read[VertexTaskCounts] and
      (JsPath \ "plan").read[JobPlan]
  )(Job.apply _)
}