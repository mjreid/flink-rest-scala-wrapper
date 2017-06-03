package com.github.mjreid.flinkwrapper

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * JobsList is the result of a call to GET /jobs. It contains IDs of the jobs grouped by their statuses.
  */
case class JobsList(
  running: Seq[String],
  finished: Seq[String],
  cancelled: Seq[String],
  failed: Seq[String]
)

object JobsList {
  implicit val reads: Reads[JobsList] = (
    (JsPath \ "jobs-running").read[Seq[String]] and
      (JsPath \ "jobs-finished").read[Seq[String]] and
      (JsPath \ "jobs-cancelled").read[Seq[String]] and
      (JsPath \ "jobs-failed").read[Seq[String]]
    )(JobsList.apply _)
}
