package com.github.mjreid.flinkwrapper

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class FlinkClusterOverview(
  taskManagers: Int,
  totalSlots: Int,
  availableSlots: Int,
  runningJobs: Int,
  finishedJobs: Int,
  cancelledJobs: Int,
  failedJobs: Int
)

object FlinkClusterOverview {
  implicit val reads: Reads[FlinkClusterOverview] = (
    (JsPath \ "taskmanagers").read[Int] and
      (JsPath \ "slots-total").read[Int] and
      (JsPath \ "slots-available").read[Int] and
      (JsPath \ "jobs-running").read[Int] and
      (JsPath \ "jobs-finished").read[Int] and
      (JsPath \ "jobs-cancelled").read[Int] and
      (JsPath \ "jobs-failed").read[Int]
    )(FlinkClusterOverview.apply _)
}
