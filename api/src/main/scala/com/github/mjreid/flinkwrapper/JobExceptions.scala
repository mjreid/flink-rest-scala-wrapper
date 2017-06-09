package com.github.mjreid.flinkwrapper

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class JobExceptions(
  rootException: Option[String],
  allExceptions: Seq[TaskException],
  truncated: Boolean
)

object JobExceptions {
  implicit val reads: Reads[JobExceptions] = (
    (JsPath \ "root-exception").readNullable[String] and
      (JsPath \ "all-exceptions").read[Seq[TaskException]] and
      (JsPath \ "truncated").read[Boolean]
  )(JobExceptions.apply _)
}

case class TaskException(
  exception: String,
  task: String,
  location: String
)

object TaskException {
  implicit val reads: Reads[TaskException] = (
    (JsPath \ "exception").read[String] and
      (JsPath \ "task").read[String] and
      (JsPath \ "location").read[String]
  )(TaskException.apply _)
}