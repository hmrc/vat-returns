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
import connectors.httpParsers.VatReturnsHttpParser
import mocks.MockHttp
import models._
import play.api.http.Status
import models.VatReturnFilters._
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.RequestTimeoutException

import scala.concurrent.Future

class SubmitVatReturnConnectorSpec extends SpecBase with MockHttp {

  val testReturn: VatReturn =
    VatReturn(
      VatReturnIdentification("VRN", "555555555"),
      VatReturnDetail(
        "17AA",
        1.23,
        1.23,
        1.23,
        1.23,
        1.23,
        1.23,
        1.23,
        1.23,
        1.23
      )
    )

  val testVrn: String = "555555555"
  val connector = new VatReturnsConnector(mockHttpGet, mockAppConfig)

  "The VatReturnsConnector" should {

    "format the request url correctly for vat-returns DES requests" in {
      val actualUrl: String = connector.setupDesVatReturnsUrl(testVrn)
      val expectedUrl: String = s"${mockAppConfig.desServiceUrl}${mockAppConfig.setupDesReturnsStartPath}$testVrn"
      actualUrl shouldBe expectedUrl
    }

    "return a VatReturn model" when {

      "calling for a user with all Query Parameters defined and a success response received" in {

        val successResponse = Right(testReturn)
        setupMockHttpGet(connector.setupDesVatReturnsUrl(testVrn),
                         Seq(periodKeyValue -> "17AA"))(Future.successful(successResponse))
        val result: Future[VatReturnsHttpParser.HttpGetResult[VatReturnDetail]] = connector.getVatReturns(
          vrn = testVrn,
          queryParameters = VatReturnFilters("17AA")
        )
        await(result) shouldBe successResponse
      }
    }

    "return an Error model" when {

      "calling for a user with non-success response received, single error" in {
        val badRequestSingleError = Left(ErrorResponse(Status.BAD_REQUEST, Error(code = "CODE", reason = "ERROR MESSAGE")))
        setupMockHttpGet(connector.setupDesVatReturnsUrl(testVrn),
                         Seq(periodKeyValue -> "17AA"))(Future.successful(badRequestSingleError))
        val result: Future[VatReturnsHttpParser.HttpGetResult[VatReturnDetail]] = connector.getVatReturns(
          vrn = testVrn,
          queryParameters = VatReturnFilters("17AA")
        )
        await(result) shouldBe badRequestSingleError
      }

      "calling for a user with non-success response received, multi error" in {

        val badRequestMultiError = Left(ErrorResponse(Status.BAD_REQUEST, MultiError(
          failures = Seq(
            Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
            Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
          )
        )))
        setupMockHttpGet(connector.setupDesVatReturnsUrl(testVrn),
                         Seq(periodKeyValue -> "17AA"))(Future.successful(badRequestMultiError))
        val result: Future[VatReturnsHttpParser.HttpGetResult[VatReturnDetail]] = connector.getVatReturns(
          vrn = testVrn,
          queryParameters = VatReturnFilters("17AA")
        )
        await(result) shouldBe badRequestMultiError
      }

      "a HTTP exception is received" in {

        val exception = new RequestTimeoutException("Request timed out!!!")
        setupMockHttpGet(connector.setupDesVatReturnsUrl(testVrn), Seq(periodKeyValue -> "17AA"))(Future.failed(exception))
        val result: Future[VatReturnsHttpParser.HttpGetResult[VatReturnDetail]] = connector.getVatReturns(
          vrn = testVrn,
          queryParameters = VatReturnFilters("17AA")
        )
        await(result) shouldBe Left(ErrorResponse(Status.BAD_GATEWAY, Error("BAD_GATEWAY", exception.message)))
      }
    }
  }
}
