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

package handlers

import base.SpecBase
import config.MicroserviceAppConfig
import models.{Error, VatReturnDetail}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.BearerTokenExpired
import uk.gov.hmrc.http.{JsValidationException, NotFoundException, Upstream4xxResponse, Upstream5xxResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

class ErrorHandlerSpec extends SpecBase {

  object TestErrorHandler extends ErrorHandler(inject[MicroserviceAppConfig], inject[AuditConnector])

  "The ErrorHandler.onClientError method" when {

    "called with a NOT_FOUND (404) error" should {

      "Return a NOT_FOUND result" which {

        val request = FakeRequest("","/test/path")
        lazy val result = await(TestErrorHandler.onClientError(request, Status.NOT_FOUND, "error"))

        "has the status NOT_FOUND (404)" in {
          status(result) shouldBe Status.NOT_FOUND
        }

        "has the expected error response body" in {
          jsonBodyOf(result) shouldBe Json.toJson(Error("NOT_FOUND", s"URI '${request.path}' not found"))
        }
      }
    }

    "called with a BAD_REQUEST (400) error" should {

      "Return a BAD_REQUEST result" which {

        val request = FakeRequest("","/test/path")
        lazy val result = await(TestErrorHandler.onClientError(request, Status.BAD_REQUEST, "Invalid Banana"))

        "has the status BAD_REQUEST (400)" in {
          status(result) shouldBe Status.BAD_REQUEST
        }

        "has the expected error response body" in {
          jsonBodyOf(result) shouldBe Json.toJson(Error("BAD_REQUEST", s"Bad Request. Message: 'Invalid Banana'"))
        }
      }
    }

    "called with any other error client error (e.g. PRECONDITION_FAILED)" should {

      "Return a PRECONDITION_FAILED result" which {

        val request = FakeRequest("","/test/path")
        lazy val result = await(TestErrorHandler.onClientError(request, Status.PRECONDITION_FAILED, "Precondition Error"))

        "has the status PRECONDITION_FAILED (412)" in {
          status(result) shouldBe Status.PRECONDITION_FAILED
        }

        "has the expected error response body" in {
          jsonBodyOf(result) shouldBe Json.toJson(Error(Status.PRECONDITION_FAILED.toString, "Precondition Error"))
        }
      }
    }
  }

  "The ErrorHandler.onServerError method" when {

    "triggered by a NOT_FOUND (404) exception" should {

      "Return a NOT_FOUND result" which {

        val request = FakeRequest("","/test/path")
        val error = new NotFoundException("Not Found Error")
        lazy val result = await(TestErrorHandler.onServerError(request, error))

        "has the status NOT_FOUND (404)" in {
          status(result) shouldBe Status.NOT_FOUND
        }

        "has the expected error response body" in {
          jsonBodyOf(result) shouldBe Json.toJson(Error(Status.NOT_FOUND.toString, s"Not Found Error"))
        }
      }
    }

    "triggered by a Authorisation exception (e.g. BearerTokenExpired)" should {

      "Return an UNAUTHORISED result" which {

        val request = FakeRequest("","/test/path")
        val error = new BearerTokenExpired
        lazy val result = await(TestErrorHandler.onServerError(request, error))

        "has the status UNAUTHORISED (401)" in {
          status(result) shouldBe Status.UNAUTHORIZED
        }

        "has the expected error response body" in {
          jsonBodyOf(result) shouldBe Json.toJson(Error(Status.UNAUTHORIZED.toString, s"Bearer token expired"))
        }
      }
    }

    "triggered by a JsonValidation exception" should {

      "Return an Internal Server Error (500) result" which {

        val request = FakeRequest("","/test/path")
        val error = new JsValidationException("method", "/url", VatReturnDetail.getClass, "errors")
        lazy val result = await(TestErrorHandler.onServerError(request, error))

        "has the status Internal Server Error (500)" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "has the expected error response body" in {
          jsonBodyOf(result) shouldBe Json.toJson(Error(Status.INTERNAL_SERVER_ERROR.toString,
            "method of '/url' returned invalid json. Attempting to convert to models.VatReturnDetail$ gave errors: errors"))
        }
      }
    }

    "triggered by a RunTime exception" should {

      "Return an Internal Server Error (500) result" which {

        val request = FakeRequest("","/test/path")
        val error = new RuntimeException("Runtime Error")
        lazy val result = await(TestErrorHandler.onServerError(request, error))

        "has the status Internal Server Error (500)" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "has the expected error response body" in {
          jsonBodyOf(result) shouldBe Json.toJson(Error(Status.INTERNAL_SERVER_ERROR.toString, s"Runtime Error"))
        }
      }
    }

    "triggered by an Upstream4xxResponse exception" should {

      "Return an Upstream4xxResponse result" which {

        val request = FakeRequest("","/test/path")
        val error = Upstream4xxResponse("Upstream  400 Error", Status.BAD_REQUEST, Status.BAD_REQUEST)
        lazy val result = await(TestErrorHandler.onServerError(request, error))

        "has the status BAD_REQUEST (400)" in {
          status(result) shouldBe Status.BAD_REQUEST
        }

        "has the expected error response body" in {
          jsonBodyOf(result) shouldBe Json.toJson(Error(Status.BAD_REQUEST.toString, "Upstream  400 Error"))
        }
      }
    }

    "triggered by an Upstream5xxResponse exception" should {

      "Return an Upstream5xxResponse result" which {

        val request = FakeRequest("","/test/path")
        val error = Upstream5xxResponse("Upstream  500 Error", Status.INTERNAL_SERVER_ERROR, Status.INTERNAL_SERVER_ERROR)
        lazy val result = await(TestErrorHandler.onServerError(request, error))

        "has the status BAD_REQUEST (400)" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "has the expected error response body" in {
          jsonBodyOf(result) shouldBe Json.toJson(Error(Status.INTERNAL_SERVER_ERROR.toString, "Upstream  500 Error"))
        }
      }
    }
  }
}
