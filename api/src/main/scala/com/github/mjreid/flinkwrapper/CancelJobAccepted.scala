package com.github.mjreid.flinkwrapper

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class CancelJobAccepted(
  requestId: Long,
  location: String
)

case class CancellationStatusInfo(
  status: CancellationStatus.CancellationStatus,
  requestId: Long,
  savepointPath: Option[String],
  failureCause: Option[String]
)

object CancellationStatus {

  sealed trait CancellationStatus
  case object InProgress extends CancellationStatus
  case object Success extends CancellationStatus
  case object Failed extends CancellationStatus

  implicit val reads: Reads[CancellationStatus] = Reads {
    case JsString(statusString) =>
      statusString.toUpperCase match {
        case "IN-PROGRESS" => JsSuccess(InProgress)
        case "SUCCESS" => JsSuccess(Success)
        case "FAILED" => JsSuccess(Failed)
        case _ => JsError("Not a valid cancellation status")
      }
    case _ => JsError("Not a valid cancellation status")
  }
}

object CancelJobAccepted {
  implicit val reads: Reads[CancelJobAccepted] = (
    (JsPath \ "request-id").read[Long] and
      (JsPath \ "location").read[String]
  )(CancelJobAccepted.apply _)
}

object CancellationStatusInfo {
 implicit val reads: Reads[CancellationStatusInfo] = (
   (JsPath \ "status").read[CancellationStatus.CancellationStatus] and
     (JsPath \ "request-id").read[Long] and
     (JsPath \ "savepoint-path").readNullable[String] and
     (JsPath \ "cause").readNullable[String]
 )(CancellationStatusInfo.apply _)
}