/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDateTime

import helpers.ComponentSpecBase
import helpers.servicemocks.NrsStub._
import models.Error
import models.nrs._
import models.nrs.identityData._
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.ExecutionContext.Implicits.global

class NrsConnectorISpec extends ComponentSpecBase {
  implicit def intToString: Int => String = _.toString

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
      searchKeys = SearchKeys("123456789", "18AA"),
      receiptData = None
    )
  )

  private val CHECKSUM_FAILED = 419

  val httpClient: HttpClient = app.injector.instanceOf[HttpClient]
  val connector: NrsConnector = new NrsConnector(httpClient, mockAppConfig)

  "nrsReceiptSubmission is called and the feature switch is off" should {
    
    "return success" when {
      
      "all values are valid" in {
        val successResponse: NrsReceiptSuccessModel = NrsReceiptSuccessModel("submission successful")

        stubSubmissionResponse(ACCEPTED, Right(successResponse), vrn = None)

        val result = {
          mockAppConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Right(successResponse)
      }
    }

    "return an error" when {

      "the request parameters are invalid (BAD_REQUEST)" in {
        val expectedResponse = Error(BAD_REQUEST, "Request parameters are invalid")

        stubSubmissionResponse(BAD_REQUEST, Left(Error(BAD_REQUEST, "This model doesn't really matter here")), vrn = None)

        val result = {
          mockAppConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }

      "the API key is wrong (UNAUTHORIZED)" in {
        val expectedResponse = Error(UNAUTHORIZED, "X-API-Key is either invalid, or missing.")

        stubSubmissionResponse(UNAUTHORIZED, Left(Error(UNAUTHORIZED, "This model doesn't really matter here")), vrn = None)

        val result = {
          mockAppConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }

      "the checksum fails (Custom 419)" in {
        val expectedResponse = Error(CHECKSUM_FAILED, "The provided Sha256Checksum provided does not match the decoded payload Sha256Checksum.")

        stubSubmissionResponse(CHECKSUM_FAILED, Left(Error(CHECKSUM_FAILED, "This model doesn't really matter here")), vrn = None)

        val result = {
          mockAppConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }

      "any other code is received" in {
        val expectedResponse = Error(INTERNAL_SERVER_ERROR, Json.toJson(Error(INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR")).toString())

        stubSubmissionResponse(INTERNAL_SERVER_ERROR, Left(Error(INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR")), vrn = None)

        val result = {
          mockAppConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
    }
  }

  "nrsReceiptSubmission is called and the feature switch is on" should {

    "return success" when {

      "all values are valid" in {
        val successResponse: NrsReceiptSuccessModel = NrsReceiptSuccessModel("submission successful")

        stubSubmissionResponse(ACCEPTED, Right(successResponse))

        val result = await(connector.nrsReceiptSubmission(requestModel))

        result shouldBe Right(successResponse)
      }
    }

    "return an error" when {

      "the request parameters are invalid (BAD_REQUEST)" in {
        val expectedResponse = Error(BAD_REQUEST, "Request parameters are invalid")

        stubSubmissionResponse(BAD_REQUEST, Left(Error(BAD_REQUEST, "Bad Request")))

        val result = await(connector.nrsReceiptSubmission(requestModel))

        result shouldBe Left(expectedResponse)
      }

      "the API key is wrong (UNAUTHORIZED)" in {
        val expectedResponse = Error(UNAUTHORIZED, "X-API-Key is either invalid, or missing.")

        stubSubmissionResponse(UNAUTHORIZED, Left(Error(UNAUTHORIZED, "Unauthorized")))

        val result = await(connector.nrsReceiptSubmission(requestModel))

        result shouldBe Left(expectedResponse)
      }

      "the checksum fails (Custom 419)" in {
        val expectedResponse = Error(CHECKSUM_FAILED, "The provided Sha256Checksum provided does not match the decoded payload Sha256Checksum.")

        stubSubmissionResponse(CHECKSUM_FAILED, Left(Error(CHECKSUM_FAILED, "Checksum failure")))

        val result = await(connector.nrsReceiptSubmission(requestModel))

        result shouldBe Left(expectedResponse)
      }

      "any other code is received" in {
        val expectedResponse = Error(INTERNAL_SERVER_ERROR, Json.toJson(Error(INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR")).toString())

        stubSubmissionResponse(INTERNAL_SERVER_ERROR, Left(Error(INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR")))

        val result = await(connector.nrsReceiptSubmission(requestModel))

        result shouldBe Left(expectedResponse)
      }
    }
  }
}
