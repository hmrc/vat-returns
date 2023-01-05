/*
 * Copyright 2023 HM Revenue & Customs
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

import models._
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object SubmitVatReturnHttpParser extends ResponseHttpParsers {

  implicit object SubmitVatReturnReads extends HttpReads[HttpGetResult[SuccessModel]] {

    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[SuccessModel] = {
      response.status match {
        case OK => response.json.validate[SuccessModel].fold(
          _ => {
            logger.warn("[SubmitVatReturnReads][read] DES response did not contain formBundleNumber")
            Left(UnexpectedJsonFormat)
          },
          valid => {
            logger.debug(s"[SubmitVatReturnReads][read] Successful response from submission. Body: ${response.body}")
            Right(valid)
          }
        )
        case status =>
          logger.warn(s"[SubmitVatReturnReads][read] Unexpected Response. Status: $status. Body: ${response.body}")
          handleErrorResponse(response)
      }
    }
  }
}
