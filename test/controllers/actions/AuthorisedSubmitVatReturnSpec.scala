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

package controllers.actions

import base.SpecBase
import mocks.auth.MockAuthConnector
import models.Error
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.~
import scala.concurrent.Future

class AuthorisedSubmitVatReturnSpec extends SpecBase with MockAuthConnector {

  object TestAuthorisedSubmitVatReturn extends AuthorisedSubmitVatReturn(mockAuthConnector, controllerComponents)

  def action(vrn: String): Future[Result] = TestAuthorisedSubmitVatReturn.async(vrn) {
    _ =>
      Future.successful(Ok)
  } (ec)(fakeRequest)

  "Calling .async" when {

    "user is non-Agent" when {

      "user has HMRC-MTD-VAT enrolment" when {

        "enrolment VRN is same as requested VRN" should {

          val enrolments = Enrolments(Set(Enrolment("HMRC-MTD-VAT").withIdentifier("VRN", "999999999")))
          lazy val result = action("999999999")

          "return 200" in {
            mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
            status(result) shouldBe Status.OK
          }
        }

        "enrolment VRN is different from request VRN" should {

          val enrolments = Enrolments(Set(Enrolment("HMRC-MTD-VAT").withIdentifier("VRN", "123456789")))
          lazy val result = action("999999999")

          "return 403" in {
            mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
            status(result) shouldBe Status.FORBIDDEN
          }

          "return a reason in JSON body" in {
            jsonBodyOf(await(result)) shouldBe Json.toJson(Error(
              "403",
              "Forbidden access to vat-returns service. Requested VRN does not match VRN in auth header")
            )
          }
        }
      }

      "user does not have HMRC-MTD-VAT enrolment" should {

        val enrolments = Enrolments(Set(Enrolment("SOME-OTHER-ENROLMENT").withIdentifier("CTUTR", "123456789")))
        lazy val result = action("999999999")

        "return FORBIDDEN status" in {
          mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
          status(result) shouldBe Status.FORBIDDEN
        }

        "return a reason in JSON body" in {
          jsonBodyOf(await(result)) shouldBe Json.toJson(Error(
            "403",
            "Forbidden access to vat-returns service. User does not have HMRC-MTD-VAT enrolment")
          )
        }
      }
    }

    "user is an Agent" when {

      "user has HMRC-AS-AGENT enrolment" when {

        "user has a delegated HMRC-MTD-VAT enrolment" when {

          val enrolments = Enrolments(Set(Enrolment("HMRC-AS-AGENT").withIdentifier("AgentReferenceNumber", "XAIT12234567")))
          lazy val result = action("999999999")

          "return 200" in {
            mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Agent), enrolments)))
            mockAuthoriseAgent()(Future.successful(enrolments))
            status(result) shouldBe Status.OK
          }
        }

        "user does not have a delegated HMRC-MTD-VAT enrolment" should {

          val enrolments = Enrolments(Set(Enrolment("HMRC-AS-AGENT").withIdentifier("AgentReferenceNumber", "XAIT12234567")))
          lazy val result = action("999999999")

          "return FORBIDDEN status" in {
            mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Agent), enrolments)))
            mockAuthoriseAgent()(Future.failed(InsufficientEnrolments()))
            status(result) shouldBe Status.FORBIDDEN
          }

          "return a reason in JSON body" in {
            jsonBodyOf(await(result)) shouldBe Json.toJson(Error(
              "403",
              "Forbidden access to vat-returns service. Insufficient Enrolments")
            )
          }
        }
      }

      "user does not have HMRC-AS-AGENT enrolment" should {

        val enrolments = Enrolments(Set(Enrolment("SOME-OTHER-ENROLMENT").withIdentifier("CTUTR", "123456789")))
        lazy val result = action("999999999")

        "return FORBIDDEN status" in {
          mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Agent), enrolments)))
          mockAuthoriseAgent()(Future.successful(enrolments))
          status(result) shouldBe Status.FORBIDDEN
        }

        "return a reason in JSON body" in {
          jsonBodyOf(await(result)) shouldBe Json.toJson(Error(
            "403",
            "Forbidden access to vat-returns service. User does not have HMRC-AS-AGENT enrolment")
          )
        }
      }
    }

    "no session exists" should {

      lazy val result = action("999999999")

      "return UNAUTHORIZED status" in {
        mockAuthorise()(Future.failed(MissingBearerToken()))
        status(result) shouldBe Status.UNAUTHORIZED
      }

      "return a reason in JSON body" in {
        jsonBodyOf(await(result)) shouldBe Json.toJson(Error(
          "401",
          "User has no active session")
        )
      }
    }
  }
}
