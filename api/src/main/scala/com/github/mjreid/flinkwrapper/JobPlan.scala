package com.github.mjreid.flinkwrapper

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class JobPlan(
  jobId: String,
  name: String,
  nodes: Seq[JobPlanNode]
)

object JobPlan {
  implicit val reads: Reads[JobPlan] = (
    (JsPath \ "jid").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "nodes").read[Seq[JobPlanNode]]
    )(JobPlan.apply _)
}
