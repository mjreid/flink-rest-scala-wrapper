package com.github.mjreid.flinkwrapper.util

import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.concurrent.TimeUnit

import play.api.libs.json._

import scala.concurrent.duration.{Duration, FiniteDuration}

/**
  * Readers contains some play-json Reads formatters that are used in various case classes.
  */
private[flinkwrapper] object Readers {
  /**
    * millisLocalDateTimeReader reads milliseconds into a LocalDateTime.
    */
  val millisLocalDateTimeReader: Reads[LocalDateTime] = Reads {
    case JsNumber(valueBigDecimal) =>
      if (valueBigDecimal.isValidLong)
        JsSuccess[LocalDateTime](Instant.ofEpochMilli(valueBigDecimal.toLongExact).atZone(ZoneId.systemDefault()).toLocalDateTime)
      else
        JsError("Not a valid long")
    case _ =>
      JsError("Not a valid duration")
  }

  /**
    * millisDurationReader reads milliseconds into a Duration.
    */
  val millisDurationReader: Reads[Duration] = Reads {
    case JsNumber(valueBigDecimal) =>
      if (valueBigDecimal.isValidLong)
        JsSuccess[Duration](new FiniteDuration(valueBigDecimal.toLong, TimeUnit.MILLISECONDS))
      else
        JsError("Not a valid long")
    case _ =>
      JsError("Not a valid duration")
  }

  val millisOptionalDurationReader: Reads[Option[Duration]] = Reads {
    case JsNumber(valueBigDecimal) =>
      if (valueBigDecimal.isValidLong) {
        val millis = valueBigDecimal.toLongExact
        if (millis > 0) {
          JsSuccess[Option[Duration]](Some(new FiniteDuration(millis, TimeUnit.MILLISECONDS)))
        } else {
          JsSuccess(None)
        }
      } else {
        JsError("Not a valid duration")
      }
    case _ =>
      JsError("Not a valid duration")
  }

  /**
    * millisOptionalDateTimeReader reads milliseconds into an Option[LocalDateTime]. If the millis value is <= 0, the
    * value is None. (The Flink API seems to alternate between returning 0 and -1 to indicate unset.)
    */
  val millisOptionalDateTimeReader: Reads[Option[LocalDateTime]] = Reads {
    case JsNumber(valueBigDecimal) =>
      if (valueBigDecimal.isValidLong) {
        val millis = valueBigDecimal.toLongExact
        if (millis > 0) {
          JsSuccess[Option[LocalDateTime]](Some(Instant.ofEpochMilli(valueBigDecimal.toLongExact).atZone(ZoneId.systemDefault()).toLocalDateTime))
        } else {
          JsSuccess(None)
        }
      } else {
        JsError("Not a valid long")
      }
    case _ =>
      JsError("Not a valid duration")
  }

  /**
    * flinkKeyValueReader reads properties in the Flink format (a JS object with "name" and "value" keys) and puts
    * them into a map.
    */
  val flinkKeyValueReader: Reads[Map[String, String]] = Reads {
    case JsArray(objects) =>
      val mapEntries = objects.flatMap {
              case JsObject(underlying) =>
                Some((underlying.getOrElse("name", JsString("")).toString(),
                  underlying.getOrElse("value", JsString("")).toString()))
              case _ =>
                None
            }
      JsSuccess(mapEntries.toMap)
    case _ =>
      JsError("Unexpected key/value format")
  }
}
