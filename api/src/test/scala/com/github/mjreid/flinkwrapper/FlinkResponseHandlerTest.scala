package com.github.mjreid.flinkwrapper

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.ws.StandaloneWSResponse

class FlinkResponseHandlerTest extends FlatSpec with Matchers with MockFactory {

  case class Dummy(foo: String)
  object Dummy {
    implicit val reads: Reads[Dummy] = (JsPath \ "foo").read[String].map(s => Dummy(s))
  }

  private val serializableObject = Dummy("test")
  private val serializableObjectJson = """{"foo":"test"}"""

  "handleResponse" should "throw correct exception if status code is greater than 400" in {
    val wsResponse = stub[StandaloneWSResponse]
    (wsResponse.body _).when().returns(serializableObjectJson)
    (wsResponse.status _).when().returns(400)

    val caught = intercept[FlinkWrapperUnexpectedStatusCodeException] {
      FlinkResponseHandler.handleResponse[Dummy](wsResponse)
    }

    caught.code should be(400)
    caught.responseBody should be(serializableObjectJson)
  }

  it should "parse a response body correctly" in {
    val wsResponse = stub[StandaloneWSResponse]
    (wsResponse.body _).when().returns(serializableObjectJson)
    (wsResponse.status _).when().returns(200)

    val result = FlinkResponseHandler.handleResponse[Dummy](wsResponse)
    result should be(serializableObject)
  }

  "handleResponseWith404" should "return none if status code is a 404" in {
    val wsResponse = stub[StandaloneWSResponse]
    (wsResponse.body _).when().returns("Object is not found")
    (wsResponse.status _).when().returns(404)

    val result = FlinkResponseHandler.handleResponseWith404[Dummy](wsResponse)
    result should be(None)
  }

  it should "throw an exception if status code is greater than 400 (other than 404)" in {
    val wsResponse = stub[StandaloneWSResponse]
    (wsResponse.body _).when().returns(serializableObjectJson)
    (wsResponse.status _).when().returns(500)

    val caught = intercept[FlinkWrapperUnexpectedStatusCodeException] {
      FlinkResponseHandler.handleResponseWith404[Dummy](wsResponse)
    }

    caught.code should be(500)
    caught.responseBody should be(serializableObjectJson)
  }

  it should "parse a response body correctly" in {
    val wsResponse = stub[StandaloneWSResponse]
    (wsResponse.body _).when().returns(serializableObjectJson)
    (wsResponse.status _).when().returns(200)

    val result = FlinkResponseHandler.handleResponseWith404[Dummy](wsResponse)
    result should be(Some(serializableObject))
  }

  "handleResponseIgnoreStatusCodes" should "parse a response body correctly even if status code is 500" in {
    val wsResponse = stub[StandaloneWSResponse]
    (wsResponse.body _).when().returns(serializableObjectJson)
    (wsResponse.status _).when().returns(500)

    val result = FlinkResponseHandler.handleResponseIgnoreStatusCodes[Dummy](wsResponse)
    result should be(serializableObject)
  }

  "parseOrThrow" should "return the expected exception if the response is not valid JSON" in {
    val garbage = "G{\"}RBAGE"

    val caught = intercept[FlinkWrapperInvalidJsonException] {
      FlinkResponseHandler.parseOrThrow[Dummy](garbage)
    }

    caught.getMessage shouldNot be("")
  }

  it should "return the expected exception if the response JSON does not match the deserialization target" in {
    val incorrectJson = """ {"field":"value"}  """

    val caught = intercept[FlinkWrapperInvalidJsonException] {
      FlinkResponseHandler.parseOrThrow[Dummy](incorrectJson)
    }

    caught.getMessage shouldNot be("")
  }

}
