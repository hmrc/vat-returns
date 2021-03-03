/*
 * Copyright 2021 HM Revenue & Customs
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

  val successResponse: Either[Nothing, VatReturn] = Right(testReturn)
  val badRequestSingleError: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, Error(code = "CODE", reason = "ERROR MESSAGE")))
  val badRequestMultiError = Left(ErrorResponse(Status.BAD_REQUEST, MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
    )
  )))

  val testVrn: String = "555555555"

  object TestVatReturnsConnector extends VatReturnsConnector(mockHttpGet, mockAppConfig)

  "The VatReturnsConnector" should {

    "format the request url correctly for vat-returns DES requests" in {
      val actualUrl: String = TestVatReturnsConnector.setupDesVatReturnsUrl(testVrn)
      val expectedUrl: String = s"${mockAppConfig.desServiceUrl}${mockAppConfig.setupDesReturnsStartPath}$testVrn"
      actualUrl shouldBe expectedUrl
    }

    "when calling the getVatReturns" when {
      "calling for a user with all Query Parameters defined and a success response received" should {

        "return a VatReturn model" in {
          setupMockHttpGet(TestVatReturnsConnector.setupDesVatReturnsUrl(testVrn), Seq(
            periodKeyValue -> "17AA"
          ))(successResponse)
          val result: Future[VatReturnsHttpParser.HttpGetResult[VatReturnDetail]] = TestVatReturnsConnector.getVatReturns(
            vrn = testVrn,
            queryParameters = VatReturnFilters("17AA")
          )
          await(result) shouldBe successResponse
        }
      }
    }

    "calling for a user with non-success response received, single error" should {

      "return a Error model" in {
        setupMockHttpGet(TestVatReturnsConnector.setupDesVatReturnsUrl(testVrn), Seq(
          periodKeyValue -> "17AA"
        ))(badRequestSingleError)
        val result: Future[VatReturnsHttpParser.HttpGetResult[VatReturnDetail]] = TestVatReturnsConnector.getVatReturns(
          vrn = testVrn,
          queryParameters = VatReturnFilters("17AA")
        )
        await(result) shouldBe badRequestSingleError
      }
    }

    "calling for a user with non-success response received, multi error" should {

      "return a MultiError model" in {
        setupMockHttpGet(TestVatReturnsConnector.setupDesVatReturnsUrl(testVrn), Seq(
          periodKeyValue -> "17AA"
        ))(badRequestMultiError)
        val result: Future[VatReturnsHttpParser.HttpGetResult[VatReturnDetail]] = TestVatReturnsConnector.getVatReturns(
          vrn = testVrn,
          queryParameters = VatReturnFilters("17AA")
        )
        await(result) shouldBe badRequestMultiError
      }
    }
  }
}
