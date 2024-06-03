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

package models

import play.api.http.Status
import play.api.libs.json.{Format, JsValue, Json, Writes}

sealed trait Errors {
  def toJson: JsValue
}

case class Error(code: String, reason: String) extends Errors {
  override def toJson: JsValue = Json.obj(
    "code" -> code,
    "reason" -> reason
  )
}

object Error {
  implicit val format: Format[Error] = Json.format[Error]
}

case class MultiError(failures: Seq[Error]) extends Errors {
  override def toJson: JsValue = Json.obj(
    "failures" -> failures.map(_.toJson)
  )
}

object MultiError {
  implicit val format: Format[MultiError] = Json.format[MultiError]
}

case class ErrorResponse(status: Int, error: Errors) {
  def toJson: JsValue = Json.obj(
    "status" -> status,
    "error" -> error.toJson
  )
}

object UnauthenticatedError extends Error(
  code = "UNAUTHENTICATED",
  reason = "Not authenticated"
){implicit val writes: Writes[UnauthenticatedError.type] = Writes(model => Json.toJson(model.asInstanceOf[Error]))}

object ForbiddenError extends Error(
  code = "UNAUTHORISED",
  reason = "Not authorised"
){implicit val writes: Writes[ForbiddenError.type] = Writes(model => Json.toJson(model.asInstanceOf[Error]))}

object InvalidJsonResponse extends ErrorResponse(
  status = Status.INTERNAL_SERVER_ERROR,
  error = Error(
    code = "INVALID_JSON",
    reason = "The downstream service responded with invalid json."
  )
)

object UnexpectedJsonFormat extends ErrorResponse(
  status = Status.INTERNAL_SERVER_ERROR,
  error = Error(
    code = "UNEXPECTED_JSON_FORMAT",
    reason = "The downstream service responded with json which did not match the expected format."
  )
)

object UnexpectedResponse extends ErrorResponse(
  status = Status.INTERNAL_SERVER_ERROR,
  error = Error(
    code = "UNEXPECTED_DOWNSTREAM_ERROR",
    reason = "The downstream service responded with an unexpected response."
  )
)

object InvalidVrn extends Error(
  code = "ERROR_VRN_INVALID",
  reason = "The supplied Vrn is invalid."
){implicit val writes: Writes[InvalidVrn.type] = Writes(model => Json.toJson(model.asInstanceOf[Error]))}
