package com.github.mjreid.flinkwrapper

import com.github.mjreid.flinkwrapper.util.Readers
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

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