/*
 * Copyright 2022 HM Revenue & Customs
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

package services

import java.time.LocalDateTime

import base.SpecBase
import connectors.NrsConnector
import models.Error
import models.nrs._
import models.nrs.identityData._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import scala.concurrent.Future

class NrsSubmissionServiceSpec extends SpecBase with MockitoSugar {
  val requestModel: NrsReceiptRequestModel = NrsReceiptRequestModel(
    payload = "aString",
    metadata = Metadata(
      businessId = "anId",
      notableEvent = "anEvent",
      payloadContentType = AppJson,
      payloadSha256Checksum = None,
      nrSubmissionId = None,
      userSubmissionTimestamp = LocalDateTime.now(),
      identityData = IdentityData(
        credentials = Some(IdentityCredentials("someId", "someType")),
        confidenceLevel = 200,
        name = Some(IdentityName(Some("First"), Some("Last"))),
        agentInformation = IdentityAgentInformation(
          agentCode = Some("Agent Code"),
          agentFriendlyName = Some("Agent Name"),
          agentId = Some("AGNT")
        ),
        itmpName = IdentityItmpName(
          givenName = Some("Given"),
          middleName = Some("Middle"),
          familyName = Some("Last")
        ),
        itmpAddress = IdentityItmpAddress(
          line1 = Some("Line 1"),
          postCode = Some("LN11NE"),
          countryName = Some("ENGLAND"),
          countryCode = Some("EN")
        ),
        loginTimes = IdentityLoginTimes(LocalDateTime.now(), Some(LocalDateTime.now()))
      ),
      userAuthToken = "someToken",
      headerData = Map(),
      searchKeys = SearchKeys("VRN", "123456789"),
      receiptData = None
    )
  )

  val successResponse = NrsReceiptSuccessModel("someSuccessfulIdString")
  val someError = Error("dis a code", "dis a message")

  val mockConnector: NrsConnector = mock[NrsConnector]
  val service: NrsSubmissionService = new NrsSubmissionService(mockConnector)

  "nrsReceiptSubmission" should {
    "return what is handed back to it" when {
      "receiving a successful response" in {
        when(
          mockConnector.nrsReceiptSubmission(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any())
        ).thenReturn(
          Future.successful(Right(successResponse))
        )

        val result = await(service.nrsReceiptSubmission(requestModel)(ec, hc))

        result shouldBe Right(successResponse)
      }
      "receiving an error response" in {
        when(
          mockConnector.nrsReceiptSubmission(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any())
        ).thenReturn(
          Future.successful(Left(someError))
        )

        val result = await(service.nrsReceiptSubmission(requestModel))

        result shouldBe Left(someError)
      }
    }
  }
}
