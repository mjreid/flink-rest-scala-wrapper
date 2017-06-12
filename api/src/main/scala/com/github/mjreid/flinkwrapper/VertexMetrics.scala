package com.github.mjreid.flinkwrapper

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class VertexMetrics(
  readBytes: Long,
  writeBytes: Long,
  readRecords: Long,
  writeRecords: Long
)

object VertexMetrics {
  implicit val reads: Reads[VertexMetrics] = (
    (JsPath \ "read-bytes").read[Long] and
      (JsPath \ "write-bytes").read[Long] and
      (JsPath \ "read-records").read[Long] and
      (JsPath \ "write-records").read[Long]
    )(VertexMetrics.apply _)
}
