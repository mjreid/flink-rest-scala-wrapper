package com.github.mjreid.flinkwrapper

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class NodeInput(
  num: Long,
  id: String,
  shipStrategy: Option[String],
  localStrategy: Option[String],
  caching: Option[String],
  exchange: String
)

object NodeInput {
  implicit val reads: Reads[NodeInput] = (
    (JsPath \ "num").read[Long] and
      (JsPath \ "id").read[String] and
      (JsPath \ "ship_strategy").readNullable[String] and
      (JsPath \ "local_strategy").readNullable[String] and
      (JsPath \ "caching").readNullable[String] and
      (JsPath \ "exchange").read[String]
    )(NodeInput.apply _)
}
