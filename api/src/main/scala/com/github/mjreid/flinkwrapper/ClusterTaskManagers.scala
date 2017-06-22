package com.github.mjreid.flinkwrapper

import java.time.LocalDateTime

import com.github.mjreid.flinkwrapper.util.Readers
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class ClusterTaskManagers(
  taskManagers: Seq[TaskManager]
)

object ClusterTaskManagers {
  implicit val reads: Reads[ClusterTaskManagers] =
    (JsPath \ "taskmanagers").read[Seq[TaskManager]]
      .map(tm => ClusterTaskManagers(tm))
}

case class TaskManager(
  id: String,
  path: String,
  dataPort: Int,
  timeSinceLastHeartbeat: LocalDateTime,
  slotsNumber: Int,
  freeSlots: Int,
  cpuCores: Int,
  physicalMemory: Long,
  freeMemory: Long,
  managedMemory: Long
)

object TaskManager {
  implicit val reads: Reads[TaskManager] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "path").read[String] and
      (JsPath \ "dataPort").read[Int] and
      (JsPath \ "timeSinceLastHeartbeat").read[LocalDateTime](Readers.millisLocalDateTimeReader) and
      (JsPath \ "slotsNumber").read[Int] and
      (JsPath \ "freeSlots").read[Int] and
      (JsPath \ "cpuCores").read[Int] and
      (JsPath \ "physicalMemory").read[Long] and
      (JsPath \ "freeMemory").read[Long] and
      (JsPath \ "managedMemory").read[Long]
    )(TaskManager.apply _)
}
