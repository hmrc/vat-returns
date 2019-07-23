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

package connectors

import java.time.LocalDateTime

import com.github.tomakehurst.wiremock.WireMockServer
import config.{AppConfig, MicroserviceAppConfig}
import helpers.{ComponentSpecBase, WireMockHelper}
import helpers.servicemocks.NrsStub._
import javax.inject.Inject
import models.Error
import models.nrs._
import models.nrs.identityData._
import play.api.{Configuration, Environment}
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.ExecutionContext.Implicits.global

class MockAppConfig @Inject()(override val environment: Environment, implicit val configuration: Configuration)
  extends MicroserviceAppConfig(environment, configuration) {
  import WireMockHelper._

  override val nrsSubmissionEndpoint: String = s"$url/submission"
}

class NrsConnectorISpec extends ComponentSpecBase {
  implicit def intToString: Int => String = _.toString

  val requestModel: NrsReceiptRequestModel = NrsReceiptRequestModel(
    "aString",
    Metadata(
      "anId", "anEvent", AppJson, None, None, LocalDateTime.now(), IdentityData(
        credentials = IdentityCredentials("someId", "someType"), confidenceLevel = 200, name = IdentityName("Dovah", "Kin"),
        agentInformation = IdentityAgentInformation("asdf", "Dragon Born", "FusRohDah"), itmpName = IdentityItmpName("Never ganna", "give you", "up"),
        itmpAddress = IdentityItmpAddress("WOAH", "WH04NOWS", "WHERE", "WH"), loginTimes = IdentityLoginTimes(LocalDateTime.now(), LocalDateTime.now())
      ), "someToken", Map(), SearchKeys("asdf", "Renaissance"), None
    )
  )

  private val CHECKSUM_FAILED = 419

  val mockConfig: MockAppConfig = app.injector.instanceOf[MockAppConfig]
  val httpClient: HttpClient = app.injector.instanceOf[HttpClient]
  val connector: NrsConnector = new NrsConnector(httpClient, mockConfig)

  "nrsReceiptSubmission is called and the feature switch is off" should {
    "return success" when {
      "all values are valid" in {
        val successResponse: NrsReceiptSuccessModel = NrsReceiptSuccessModel("submission successful")

        stubSubmissionResponse(ACCEPTED, Right(successResponse), "not-a-key")

        val result = {
          mockConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Right(successResponse)
      }
    }

    "return an error" when {
      "the request parameters are invalid (BAD_REQUEST)" in {
        val expectedResponse = Error(BAD_REQUEST, "Request parameters are invalid")

        stubSubmissionResponse(BAD_REQUEST, Left(Error(BAD_REQUEST, "This model doesn't really matter here")), "not-a-key")

        val result = {
          mockConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
      "the API key is wrong (UNAUTHORIZED)" in {
        val expectedResponse = Error(UNAUTHORIZED, "X-API-Key is either invalid, or missing.")

        stubSubmissionResponse(UNAUTHORIZED, Left(Error(UNAUTHORIZED, "This model doesn't really matter here")), "not-a-key")

        val result = {
          mockConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
      "NRS is not available (NOT_FOUND)" in {
        val expectedResponse = Error(NOT_FOUND, s"Returning response body:\n${Json.toJson(Error(NOT_FOUND, "This model doesn't really matter here"))}")

        stubSubmissionResponse(NOT_FOUND, Left(Error(NOT_FOUND, "This model doesn't really matter here")), "not-a-key")

        val result = {
          mockConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
      "the checksum fails (Custom 419)" in {
        val expectedResponse = Error(CHECKSUM_FAILED, "The provided Sha256Checksum provided does not match the decoded payload Sha256Checksum.")

        stubSubmissionResponse(CHECKSUM_FAILED, Left(Error(CHECKSUM_FAILED, "This model doesn't really matter here")), "not-a-key")

        val result = {
          mockConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
      "a 5xx is received" in {
        val expectedResponse = Error(INTERNAL_SERVER_ERROR, s"Returning response body:\n${Json.toJson(Error(CHECKSUM_FAILED,
          "This model doesn't really matter here"))}")

        stubSubmissionResponse(INTERNAL_SERVER_ERROR, Left(Error(CHECKSUM_FAILED, "This model doesn't really matter here")), "not-a-key")

        val result = {
          mockConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
      "any other code is received" in {
        val expectedResponse = Error(GONE, s"Unexpected return code, returning response body:\n${Json.toJson(Error(SEE_OTHER,
          "Where did they come from, where did they go?"))}")

        stubSubmissionResponse(GONE, Left(Error(SEE_OTHER, "Where did they come from, where did they go?")), "not-a-key")

        val result = {
          mockConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
      "there is a timeout" in {
        stubTimeoutResponse()

        val result = {
          mockConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(Error(GATEWAY_TIMEOUT, "Request to NRS timed out."))
      }
      "there is an unexpected exception" in {
        stubExceptionResponse()

        val result = {
          mockConfig.features.useStubFeature(false)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(Error("UNEXPECTED_EXCEPTION", "Remotely closed"))
      }
    }
  }

  "nrsReceiptSubmission is called and the feature switch is on" should {
    "return success" when {
      "all values are valid" in {
        val successResponse: NrsReceiptSuccessModel = NrsReceiptSuccessModel("submission successful")

        stubSubmissionResponse(ACCEPTED, Right(successResponse), "not-a-key", vrn = Some("asdf"))

        val result = {
          mockConfig.features.useStubFeature(true)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Right(successResponse)
      }
    }

    "return an error" when {
      "the request parameters are invalid (BAD_REQUEST)" in {
        val expectedResponse = Error(BAD_REQUEST, "Request parameters are invalid")

        stubSubmissionResponse(BAD_REQUEST, Left(Error(BAD_REQUEST, "This model doesn't really matter here")), "not-a-key", vrn = Some("asdf"))

        val result = {
          mockConfig.features.useStubFeature(true)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
      "the API key is wrong (UNAUTHORIZED)" in {
        val expectedResponse = Error(UNAUTHORIZED, "X-API-Key is either invalid, or missing.")

        stubSubmissionResponse(UNAUTHORIZED, Left(Error(UNAUTHORIZED, "This model doesn't really matter here")), "not-a-key", vrn = Some("asdf"))

        val result = {
          mockConfig.features.useStubFeature(true)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
      "NRS is not available (NOT_FOUND)" in {
        val expectedResponse = Error(NOT_FOUND, s"Returning response body:\n${Json.toJson(Error(NOT_FOUND, "This model doesn't really matter here"))}")

        stubSubmissionResponse(NOT_FOUND, Left(Error(NOT_FOUND, "This model doesn't really matter here")), "not-a-key", vrn = Some("asdf"))

        val result = {
          mockConfig.features.useStubFeature(true)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
      "the checksum fails (Custom 419)" in {
        val expectedResponse = Error(CHECKSUM_FAILED, "The provided Sha256Checksum provided does not match the decoded payload Sha256Checksum.")

        stubSubmissionResponse(CHECKSUM_FAILED, Left(Error(CHECKSUM_FAILED, "This model doesn't really matter here")), "not-a-key", vrn = Some("asdf"))

        val result = {
          mockConfig.features.useStubFeature(true)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
      "a 5xx is received" in {
        val expectedResponse = Error(INTERNAL_SERVER_ERROR, s"Returning response body:\n${Json.toJson(Error(CHECKSUM_FAILED,
          "This model doesn't really matter here"))}")

        stubSubmissionResponse(INTERNAL_SERVER_ERROR, Left(Error(CHECKSUM_FAILED, "This model doesn't really matter here")), "not-a-key", vrn = Some("asdf"))

        val result = {
          mockConfig.features.useStubFeature(true)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
      "any other code is received" in {
        val expectedResponse = Error(GONE, s"Unexpected return code, returning response body:\n${Json.toJson(Error(SEE_OTHER,
          "Where did they come from, where did they go?"))}")

        stubSubmissionResponse(GONE, Left(Error(SEE_OTHER, "Where did they come from, where did they go?")), "not-a-key", vrn = Some("asdf"))

        val result = {
          mockConfig.features.useStubFeature(true)
          await(connector.nrsReceiptSubmission(requestModel))
        }

        result shouldBe Left(expectedResponse)
      }
    }
  }
}
