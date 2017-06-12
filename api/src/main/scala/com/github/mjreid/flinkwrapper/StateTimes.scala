package com.github.mjreid.flinkwrapper

import java.time.LocalDateTime

import com.github.mjreid.flinkwrapper.util.Readers
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class StateTimes(
  createdTime: Option[LocalDateTime],
  runningTime: Option[LocalDateTime],
  failingTime: Option[LocalDateTime],
  failedTime: Option[LocalDateTime],
  cancellingTime: Option[LocalDateTime],
  canceledTime: Option[LocalDateTime],
  finishedTime: Option[LocalDateTime],
  restartingTime: Option[LocalDateTime],
  suspendedTime: Option[LocalDateTime],
  reconcilingTime: Option[LocalDateTime]
)

object StateTimes {
  private implicit val dateTimeReader = Readers.millisOptionalDateTimeReader

  implicit val reads: Reads[StateTimes] = (
    (JsPath \ "CREATED").read[Option[LocalDateTime]] and
      (JsPath \ "RUNNING").read[Option[LocalDateTime]] and
      (JsPath \ "FAILING").read[Option[LocalDateTime]] and
      (JsPath \ "FAILED").read[Option[LocalDateTime]] and
      (JsPath \ "CANCELLING").read[Option[LocalDateTime]] and
      (JsPath \ "CANCELED").read[Option[LocalDateTime]] and
      (JsPath \ "FINISHED").read[Option[LocalDateTime]] and
      (JsPath \ "RESTARTING").read[Option[LocalDateTime]] and
      (JsPath \ "SUSPENDED").read[Option[LocalDateTime]] and
      (JsPath \ "RECONCILING").read[Option[LocalDateTime]]
    )(StateTimes.apply _)
}
