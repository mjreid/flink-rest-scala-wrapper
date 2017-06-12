package com.github.mjreid.flinkwrapper

import java.time.LocalDateTime

import com.github.mjreid.flinkwrapper.util.Readers
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

import scala.concurrent.duration.Duration

case class JobVertex(
  id: String,
  name: String,
  parallelism: Int,
  status: ExecutionStatus.ExecutionStatus,
  startTime: Option[LocalDateTime],
  endTime: Option[LocalDateTime],
  duration: Option[Duration],
  taskCounts: VertexTaskCounts,
  metrics: VertexMetrics
)

object JobVertex {
  implicit val reads: Reads[JobVertex] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "parallelism").read[Int] and
      (JsPath \ "status").read[ExecutionStatus.ExecutionStatus] and
      (JsPath \ "start-time").read[Option[LocalDateTime]](Readers.millisOptionalDateTimeReader) and
      (JsPath \ "end-time").read[Option[LocalDateTime]](Readers.millisOptionalDateTimeReader) and
      (JsPath \ "duration").read[Option[Duration]](Readers.millisOptionalDurationReader) and
      (JsPath \ "tasks").read[VertexTaskCounts] and
      (JsPath \ "metrics").read[VertexMetrics]
    )(JobVertex.apply _)
}
