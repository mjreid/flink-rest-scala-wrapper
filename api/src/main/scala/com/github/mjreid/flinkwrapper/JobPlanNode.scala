package com.github.mjreid.flinkwrapper

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class JobPlanNode(
  id: String,
  parallelism: Int,
  operator: String,
  operatorStrategy: String,
  description: String,
  inputs: Option[Seq[NodeInput]],
  optimizerProperties: OptimizerProperties
)

object JobPlanNode {
  implicit val reads: Reads[JobPlanNode] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "parallelism").read[Int] and
      (JsPath \ "operator").read[String] and
      (JsPath \ "operator_strategy").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "inputs").readNullable[Seq[NodeInput]] and
      (JsPath \ "optimizer_properties").read[OptimizerProperties]
    )(JobPlanNode.apply _)
}
