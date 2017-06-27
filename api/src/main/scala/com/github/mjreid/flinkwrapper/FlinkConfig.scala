package com.github.mjreid.flinkwrapper

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class FlinkConfig(key: String, value: String)

object FlinkConfig {
  implicit val reads: Reads[FlinkConfig] = (
    (JsPath \ "key").read[String] and
      (JsPath \ "value").read[String]
    )(FlinkConfig.apply _)
}