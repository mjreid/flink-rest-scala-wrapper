package com.github.mjreid.flinkwrapper

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class UploadJarResult(
  status: String,
  filename: String
)

object UploadJarResult {
  implicit val reads: Reads[UploadJarResult] = (
    (JsPath \ "status").read[String] and
      (JsPath \ "filename").read[String]
  )(UploadJarResult.apply _)
}
