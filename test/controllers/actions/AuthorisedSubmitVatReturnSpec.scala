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

package controllers.actions

import base.SpecBase
import mocks.auth.MockAuthConnector
import models.Error
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import uk.gov.hmrc.auth.core._

import scala.concurrent.Future

class AuthorisedSubmitVatReturnSpec extends SpecBase with MockAuthConnector {

  object TestAuthorisedSubmitVatReturn extends AuthorisedSubmitVatReturn(mockAuthConnector)

  def action(vrn: String): Future[Result] = TestAuthorisedSubmitVatReturn.async(vrn) {
    implicit request =>
      Future.successful(Ok)
  } (ec)(fakeRequest)

  "Calling .async" when {

    "user is non-Agent" when {

      "user has HMRC-MTD-VAT enrolment" when {

        "enrolment VRN is same as requested VRN" should {

          val enrolments = Enrolments(Set(Enrolment("HMRC-MTD-VAT").withIdentifier("VRN", "999999999")))
          lazy val result = action("999999999")

          "return 200" in {
            mockAuthorise()(Future.successful(enrolments))
            status(result) shouldBe Status.OK
          }
        }

        "enrolment VRN is different from requested VRN" should {

        }
      }

      "user does not have HMRC-MTD-VAT enrolment" should {

        lazy val result = action("999999999")

        "return FORBIDDEN status" in {
          mockAuthorise()(Future.failed(InsufficientEnrolments()))
          status(result) shouldBe Status.FORBIDDEN
        }

        "return a reason in JSON body" in {
          jsonBodyOf(await(result)) shouldBe Json.toJson(Error("403", "Forbidden access to vat-returns service. Reason: Insufficient Enrolments"))
        }
      }
    }

    "user is an Agent" when {

      "user has HMRC-AS-AGENT enrolment" when {

        "user has delegated HMRC-MTD-VAT enrolment" when {

          "enrolment VRN is same as requested VRN" should {

          }

          "enrolment VRN is different from requested VRN" should {

          }
        }

        "user does not have delegated HMRC-MTD-VAT enrolment" should {

          "return FORBIDDEN status" in {

          }

          "return a reason in JSON body" in {

          }
        }
      }

      "user does not have HMRC-AS-AGENT enrolment" should {

        lazy val result = action("999999999")

        "return FORBIDDEN status" in {
          mockAuthorise()(Future.failed(InsufficientEnrolments()))
          status(result) shouldBe Status.FORBIDDEN
        }

        "return a reason in JSON body" in {
          jsonBodyOf(await(result)) shouldBe Json.toJson(Error("403", "Forbidden access to vat-returns service. Reason: Insufficient Enrolments"))
        }
      }
    }

    "no session exists" should {

      "return UNAUTHORIZED status" in {

      }

      "return a reason in JSON body" in {

      }
    }
  }
}
