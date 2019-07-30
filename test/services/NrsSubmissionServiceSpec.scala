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

import scala.concurrent.Future

class NrsSubmissionServiceSpec extends SpecBase with MockitoSugar {
  val requestModel: NrsReceiptRequestModel = NrsReceiptRequestModel(
    "aString",
    Metadata(
      "anId", "anEvent", AppJson, None, None, LocalDateTime.now(), IdentityData(
        credentials = IdentityCredentials("someId", "someType"), confidenceLevel = 200, name = IdentityName("Dovah", "Kin"),
        agentInformation = IdentityAgentInformation("asdf", "Dragon Born", "FusRohDah"), itmpName = IdentityItmpName("Never ganna", "give you", "up"),
        itmpAddress = IdentityItmpAddress("WOAH", "WH04NOWS", "WHERE", "WH"), loginTimes = IdentityLoginTimes(LocalDateTime.now(), Some(LocalDateTime.now()))
      ), "someToken", Map(), SearchKeys("asdf", "Renaissance"), None
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
