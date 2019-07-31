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

package controllers

import base.SpecBase
import controllers.actions.AuthorisedSubmitVatReturn
import mocks.auth.{MockAuthConnector, MockMicroserviceAuthorisedFunctions}
import mocks.services.MockNrsSubmissionService
import models.nrs.{NrsReceiptRequestModel, NrsReceiptSuccessModel}
import assets.NrsModelAssets._
import models.Error
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}

import scala.concurrent.Future

class NRSControllerSpec extends SpecBase with MockMicroserviceAuthorisedFunctions with MockNrsSubmissionService with MockAuthConnector {

  val correctModel = NrsReceiptRequestModel("", metadataModel)

  val incorrectModel = "bad things"

  "The POST NRSController.submitNRS method" when {

    "called by an authenticated user" when {

      object TestNRSController extends NRSController(new AuthorisedSubmitVatReturn(mockAuthConnector), mockNrsSubmissionService)

      val enrolments = Enrolments(Set(Enrolment("HMRC-MTD-VAT").withIdentifier("VRN", "999999999")))

      "a valid NRSModel is passed in the request body" should {

        lazy val result = await(TestNRSController.submitNRS("999999999")(FakeRequest().withJsonBody(Json.toJson(correctModel))))

        "return a status of 202 (ACCEPTED)" in {
          mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
          setupMockNrsReceiptSubmission(correctModel)(Right(NrsReceiptSuccessModel("1234")))
          status(result) shouldBe Status.ACCEPTED
        }

        "return a json body with the transformed des return data" in {
          jsonBodyOf(result) shouldBe Json.toJson(NrsReceiptSuccessModel("1234"))
        }
      }

      "the request body cannot be parsed to Json" should {

        lazy val result: Result = await(TestNRSController.submitNRS("999999999")(fakeRequest))

        "return a status of 400 (BAD_REQUEST)" in {
          mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
          status(result) shouldBe Status.BAD_REQUEST
        }
      }

      "the request body is not the correct model" should {

        lazy val result: Result = await(TestNRSController.submitNRS("999999999")(FakeRequest().withJsonBody(Json.toJson(incorrectModel))))

        "return a status of 400 (BAD_REQUEST)" in {
          mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
          status(result) shouldBe Status.BAD_REQUEST
        }
      }

      "the NrsSubmissionService returns an error" should {

        lazy val result: Result = await(TestNRSController.submitNRS("999999999")(FakeRequest().withJsonBody(Json.toJson(correctModel))))

        "return a status of 400 (BAD_REQUEST)" in {
          mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
          setupMockNrsReceiptSubmission(correctModel)(Left(Error("bad code", "bad reason")))
          status(result) shouldBe Status.BAD_REQUEST
        }
      }
    }
  }
}
