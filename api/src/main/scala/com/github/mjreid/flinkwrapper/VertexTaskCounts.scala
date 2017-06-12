package com.github.mjreid.flinkwrapper

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class VertexTaskCounts(
  created: Long,
  scheduled: Long,
  deploying: Long,
  running: Long,
  finished: Long,
  canceling: Long,
  canceled: Long,
  failed: Long,
  reconciling: Long
)

object VertexTaskCounts {
  implicit val reads: Reads[VertexTaskCounts] = (
    (JsPath \ "CREATED").read[Long] and
      (JsPath \ "SCHEDULED").read[Long] and
      (JsPath \ "DEPLOYING").read[Long] and
      (JsPath \ "RUNNING").read[Long] and
      (JsPath \ "FINISHED").read[Long] and
      (JsPath \ "CANCELING").read[Long] and
      (JsPath \ "CANCELED").read[Long] and
      (JsPath \ "FAILED").read[Long] and
      (JsPath \ "RECONCILING").read[Long]
    )(VertexTaskCounts.apply _)
}
