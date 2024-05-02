/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils

import java.io.File

import com.fasterxml.jackson.core.{JsonFactory, JsonParser}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jsonschema.main.JsonSchemaFactory
import play.api.libs.json.JsValue

object JsonSchemaValidator {

  def validateJsonAgainstSchema(jsonToValidate: JsValue): Boolean = {
    val jsonFactory = new JsonFactory()
    val jsonMapper = new ObjectMapper()

    val jsonSchemaLocation: File = new File("test/resources/NrsSubmissionSchema.json")
    val jsonSchema: String = scala.io.Source.fromFile(jsonSchemaLocation).mkString

    val schemaParser: JsonParser = jsonFactory.createParser(jsonSchema)
    val schemaMapping: JsonNode = jsonMapper.readTree(schemaParser)

    val inputParser = jsonFactory.createParser(jsonToValidate.toString())
    val inputMapping: JsonNode = jsonMapper.readTree(inputParser)

    JsonSchemaFactory.byDefault().getJsonSchema(schemaMapping).validate(inputMapping).isSuccess
  }
}
