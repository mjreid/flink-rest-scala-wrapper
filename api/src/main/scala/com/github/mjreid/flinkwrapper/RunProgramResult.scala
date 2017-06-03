package com.github.mjreid.flinkwrapper

import play.api.libs.json._

case class RunProgramResult(
  jobId: String
)

object RunProgramResult {
  implicit val reads: Reads[RunProgramResult] =
    (JsPath \ "jobid").read[String].map(RunProgramResult.apply)
}
