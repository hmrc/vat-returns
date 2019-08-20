/*
 * Copyright 2019 HM Revenue & Customs
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

package connectors.httpParsers

import models.Error
import models.nrs.NrsReceiptSuccessModel
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object NrsResponseParsers extends ResponseHttpParsers {
  type SubmissionResult = Either[Error, NrsReceiptSuccessModel]

  private val CHECKSUM_FAILED: Int = 419

  private implicit def intToString: Int => String = _.toString

  def handleErrorCodes(input: HttpResponse): Left[Error, Nothing] = {
    Logger.debug(s"[NrsResponseParsers][handleErrorCodes] NRS returned ${input.status}. Body: ${input.body}")
    Logger.warn(s"[NrsResponseParsers][handleErrorCodes] NRS returned ${input.status}.")

    input.status match {
      case BAD_REQUEST =>
        Left(Error(BAD_REQUEST, "Request parameters are invalid"))
      case UNAUTHORIZED =>
        Left(Error(UNAUTHORIZED, "X-API-Key is either invalid, or missing."))
      case CHECKSUM_FAILED =>
        Left(Error(CHECKSUM_FAILED, "The provided Sha256Checksum provided does not match the decoded payload Sha256Checksum."))
      case status =>
        Left(Error(status, Json.stringify(input.json)))
    }
  }

  implicit object NrsResponseReads extends HttpReads[SubmissionResult] {
    override def read(method: String, url: String, response: HttpResponse): SubmissionResult = {
      response.status match {
        case ACCEPTED => Right(response.json.as[NrsReceiptSuccessModel])
        case _ => handleErrorCodes(response)
      }
    }
  }
}
