package com.github.mjreid.flinkwrapper

import com.github.mjreid.flinkwrapper.util.Readers
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.duration.Duration

case class FlinkConfigInfo(
  refreshInterval: Int,
  timezoneOffset: Duration,
  timezoneName: String,
  flinkVersion: String,
  flinkRevision: String
)

object FlinkConfigInfo {
  implicit val reads: Reads[FlinkConfigInfo] = (
    (JsPath \ "refresh-interval").read[Int] and
      (JsPath \ "timezone-offset").read[Duration](Readers.millisDurationReader) and
      (JsPath \ "timezone-name").read[String] and
      (JsPath \ "flink-version").read[String] and
      (JsPath \ "flink-revision").read[String]
    )(FlinkConfigInfo.apply _)
}

