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

package connectors

import base.SpecBase
import connectors.httpParsers.NrsResponseParsers.SubmissionResult
import mocks.MockHttp
import models.nrs.NrsReceiptRequestModel
import models.Error
import play.api.http.Status.BAD_GATEWAY
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.RequestTimeoutException
import utils.NrsTestData.FullRequestTestData.correctModel

import scala.concurrent.Future

class NrsConnectorSpec extends SpecBase with MockHttp {

  val connector = new NrsConnector(mockHttpGet, mockAppConfig)
  val vrn = "123456789"

  "NrsConnector" should {

    "return an Error model when a HTTP exception is received" in {
      val exception = new RequestTimeoutException("Request timed out!!!")
      setupMockHttpPost[NrsReceiptRequestModel, SubmissionResult](connector.urlToUse(vrn))(Future.failed(exception))
      val result: Future[SubmissionResult] = connector.nrsReceiptSubmission(correctModel)
      await(result) shouldBe Left(Error(BAD_GATEWAY.toString, exception.message))
    }
  }
}
