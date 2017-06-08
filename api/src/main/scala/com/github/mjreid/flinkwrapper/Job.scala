package com.github.mjreid.flinkwrapper

import java.time.LocalDateTime

import com.github.mjreid.flinkwrapper.util.Readers
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.duration.Duration

case class Job(
  id: String,
  name: String,
  isStoppable: Boolean,
  state: JobStatus.JobStatus,
  startTime: LocalDateTime,
  endTime: LocalDateTime,
  duration: Duration,
  now: LocalDateTime,
  stateTimestamps: StateTimes,
  vertices: Seq[JobVertex],
  statusCounts: VertexTaskCounts,
  plan: JobPlan
)

object Job {
  implicit val reads: Reads[Job] = (
    (JsPath \ "jid").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "isStoppable").read[Boolean] and
      (JsPath \ "state").read[JobStatus.JobStatus] and
      (JsPath \ "start-time").read[LocalDateTime](Readers.millisLocalDateTimeReader) and
      (JsPath \ "end-time").read[LocalDateTime](Readers.millisLocalDateTimeReader) and
      (JsPath \ "duration").read[Duration](Readers.millisDurationReader) and
      (JsPath \ "now").read[LocalDateTime](Readers.millisLocalDateTimeReader) and
      (JsPath \ "timestamps").read[StateTimes] and
      (JsPath \ "vertices").read[Seq[JobVertex]] and
      (JsPath \ "status-counts").read[VertexTaskCounts] and
      (JsPath \ "plan").read[JobPlan]
  )(Job.apply _)
}

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

case class JobVertex(
  id: String,
  name: String,
  parallelism: Int,
  status: ExecutionStatus.ExecutionStatus,
  startTime: Option[LocalDateTime],
  endTime: Option[LocalDateTime],
  duration: Option[Duration],
  taskCounts: VertexTaskCounts,
  metrics: VertexMetrics
)

object JobVertex {
  implicit val reads: Reads[JobVertex] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "parallelism").read[Int] and
      (JsPath \ "status").read[ExecutionStatus.ExecutionStatus] and
      (JsPath \ "start-time").read[Option[LocalDateTime]](Readers.millisOptionalDateTimeReader) and
      (JsPath \ "end-time").read[Option[LocalDateTime]](Readers.millisOptionalDateTimeReader) and
      (JsPath \ "duration").read[Option[Duration]](Readers.millisOptionalDurationReader) and
      (JsPath \ "tasks").read[VertexTaskCounts] and
      (JsPath \ "metrics").read[VertexMetrics]
  )(JobVertex.apply _)
}

case class VertexTaskCounts(
  created: Long,
  scheduled: Long,
  deploying: Long,
  running: Long,
  finished: Long,
  canceling: Long,
  canceled: Long,
  failed: Long,
  reconciling: Long
)

object VertexTaskCounts {
  implicit val reads: Reads[VertexTaskCounts] = (
    (JsPath \ "CREATED").read[Long] and
      (JsPath \ "SCHEDULED").read[Long] and
      (JsPath \ "DEPLOYING").read[Long] and
      (JsPath \ "RUNNING").read[Long] and
      (JsPath \ "FINISHED").read[Long] and
      (JsPath \ "CANCELING").read[Long] and
      (JsPath \ "CANCELED").read[Long] and
      (JsPath \ "FAILED").read[Long] and
      (JsPath \ "RECONCILING").read[Long]
  )(VertexTaskCounts.apply _)
}

case class VertexMetrics(
  readBytes: Long,
  writeBytes: Long,
  readRecords: Long,
  writeRecords: Long
)

object VertexMetrics {
  implicit val reads: Reads[VertexMetrics] = (
    (JsPath \ "read-bytes").read[Long] and
      (JsPath \ "write-bytes").read[Long] and
      (JsPath \ "read-records").read[Long] and
      (JsPath \ "write-records").read[Long]
  )(VertexMetrics.apply _)
}

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

case class OptimizerProperties(
  globalProperties: Option[Map[String, String]],
  localProperties: Option[Map[String, String]],
  estimates: Option[Map[String, String]],
  costs: Option[Map[String, String]],
  compilerHints: Option[Map[String, String]]
)

object OptimizerProperties {
  implicit val reads: Reads[OptimizerProperties] = (
    (JsPath \ "global_properties").readNullable[Map[String, String]](Readers.flinkKeyValueReader) and
      (JsPath \ "local_properties").readNullable[Map[String, String]](Readers.flinkKeyValueReader) and
      (JsPath \ "estimates").readNullable[Map[String, String]](Readers.flinkKeyValueReader) and
      (JsPath \ "costs").readNullable[Map[String, String]](Readers.flinkKeyValueReader) and
      (JsPath \ "compiler_hints").readNullable[Map[String, String]](Readers.flinkKeyValueReader)
  )(OptimizerProperties.apply _)
}