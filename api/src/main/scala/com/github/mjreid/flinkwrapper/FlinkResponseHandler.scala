package com.github.mjreid.flinkwrapper

import com.fasterxml.jackson.core.JsonParseException
import play.api.libs.json._
import play.api.libs.ws.StandaloneWSResponse

trait FlinkResponseHandler {
  def handleResponse[T](response: StandaloneWSResponse)(implicit reads: Reads[T]): T
  def handleResponseWith404[T](response: StandaloneWSResponse)(implicit reads: Reads[T]): Option[T]
  def handleResponseIgnoreStatusCodes[T](response: StandaloneWSResponse)(implicit reads: Reads[T]): T
}

private[flinkwrapper] object FlinkResponseHandler extends FlinkResponseHandler {

  private val maximumBodyCharacters = 4000

  override def handleResponse[T](
    response: StandaloneWSResponse
  )(implicit reads: Reads[T]): T = {
    response.status match {
      case i if i >= 400 =>
        throw FlinkWrapperUnexpectedStatusCodeException(i, response.body.take(maximumBodyCharacters))
      case _ =>
        parseOrThrow(response.body)
    }
  }

  override def handleResponseWith404[T](
    response: StandaloneWSResponse
  )(implicit reads: Reads[T]): Option[T] = {
    response.status match {
      case 404 =>
        None
      case i if i >= 400 =>
        throw FlinkWrapperUnexpectedStatusCodeException(i, response.body.take(maximumBodyCharacters))
      case _ =>
        Some(parseOrThrow(response.body))
    }
  }

  override def handleResponseIgnoreStatusCodes[T](
    response: StandaloneWSResponse
  )(implicit reads: Reads[T]): T = {
    parseOrThrow(response.body)
  }

  private[flinkwrapper] def parseOrThrow[T](
    responseBody: String
  )(implicit reads: Reads[T]): T = {
    try {
      val json: JsValue = Json.parse(responseBody)
      json.validate[T] match {
        case JsSuccess(validatedObject, _) => validatedObject
        case JsError(errors) => throw FlinkWrapperInvalidJsonException(errors.mkString("; "))
      }
    } catch {
      case e: JsonParseException => throw FlinkWrapperInvalidJsonException("Response was not valid JSON", e)
    }
  }
}
