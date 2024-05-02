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
import mocks.MockHttp
import models.{Error, ErrorResponse, VatReturnDetail, VatReturnFilters}
import play.api.http.Status.BAD_GATEWAY
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.RequestTimeoutException

import scala.concurrent.Future

class VatReturnsConnectorSpec extends SpecBase with MockHttp {

  val connector = new VatReturnsConnector(mockHttpGet, mockAppConfig)
  val vrn = "123456789"

  "VatReturnsConnector" should {

    "return an Error model when a HTTP exception is received" in {
      val exception = new RequestTimeoutException("Request timed out!!!")
      setupMockHttpGet[VatReturnDetail](connector.setupDesVatReturnsUrl(vrn))(Future.failed(exception))
      val result = connector.getVatReturns(vrn, VatReturnFilters("#001"))
      await(result) shouldBe Left(ErrorResponse(BAD_GATEWAY, Error("BAD_GATEWAY", exception.message)))
    }
  }
}
