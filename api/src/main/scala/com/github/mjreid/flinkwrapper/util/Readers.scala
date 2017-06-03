package com.github.mjreid.flinkwrapper.util

import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.concurrent.TimeUnit

import play.api.libs.json._

import scala.concurrent.duration.{Duration, FiniteDuration}

/**
  * Readers contains some play-json Reads formatters that are used in various case classes.
  */
private[flinkwrapper] object Readers {
  val millisLocalDateTimeReader: Reads[LocalDateTime] = Reads {
    case JsNumber(valueBigDecimal) =>
      if (valueBigDecimal.isValidLong)
        JsSuccess[LocalDateTime](Instant.ofEpochMilli(valueBigDecimal.toLongExact).atZone(ZoneId.systemDefault()).toLocalDateTime)
      else
        JsError("Not a valid long")
    case _ =>
      JsError("Not a valid duration")
  }

  val millisDurationReader: Reads[Duration] = Reads {
    case JsNumber(valueBigDecimal) =>
      if (valueBigDecimal.isValidLong)
        JsSuccess[Duration](new FiniteDuration(valueBigDecimal.toLong, TimeUnit.MILLISECONDS))
      else
        JsError("Not a valid long")
    case _ =>
      JsError("Not a valid duration")
  }
}
