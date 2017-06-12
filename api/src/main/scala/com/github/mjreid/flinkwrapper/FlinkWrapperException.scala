package com.github.mjreid.flinkwrapper

/**
  * FlinkWrapperException is the base class for all exceptions returned in failed futures by the wrapper.
  * If you don't care about different error handling for invalid JSON vs timeouts (etc), just handle this.
  */
trait FlinkWrapperException extends RuntimeException

/**
  * FlinkWrapperInvalidJsonException occurs when a valid HTTP status code is returned, but there's a problem parsing
  * the response body into an object.
  */
trait FlinkWrapperInvalidJsonException extends FlinkWrapperException

object FlinkWrapperInvalidJsonException {
  def apply(message: String, cause: Throwable) = new RuntimeException(message, cause) with FlinkWrapperInvalidJsonException
  def apply(message: String) = new RuntimeException(message) with FlinkWrapperInvalidJsonException
}

/**
  * FlinkWrapperUnexpectedStatusCodeException occurs when an HTTP response has an unexpected (i.e. non-200, 204, etc)
  * status code.
  */
case class FlinkWrapperUnexpectedStatusCodeException(code: Int, responseBody: String)
  extends RuntimeException(s"Unexpected status code $code: $responseBody") with FlinkWrapperException