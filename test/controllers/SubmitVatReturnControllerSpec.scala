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

package controllers

import base.SpecBase
import controllers.actions.AuthorisedSubmitVatReturn
import mocks.auth.{MockAuthConnector, MockMicroserviceAuthorisedFunctions}
import mocks.services.MockVatReturnsService
import models.SuccessModel
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import utils.SubmitVatReturnTestData.{nonAgentVatReturnDetailModel, validVatReturnIdentificationModel}

import scala.concurrent.Future

class SubmitVatReturnControllerSpec extends SpecBase with MockVatReturnsService with MockMicroserviceAuthorisedFunctions with MockAuthConnector {

  val authorisedSubmitVatReturn = new AuthorisedSubmitVatReturn(mockAuthConnector, controllerComponents)

  object TestSubmitVatReturnController extends SubmitVatReturnController(mockVatReturnsService, authorisedSubmitVatReturn, controllerComponents)

  val enrolments = Enrolments(Set(Enrolment("HMRC-MTD-VAT").withIdentifier("VRN", "999999999")))

  "Calling the .submitVatReturn method" when {

    "called by an authenticated user" when {

      "the request is non-empty" when {

        "the user has a valid originator ID" should {

          lazy val result = TestSubmitVatReturnController.submitVatReturn("999999999")(FakeRequest().withJsonBody(
            Json.toJson(nonAgentVatReturnDetailModel)).withHeaders("OriginatorID" -> "MDTP")
          )
          val successResponse: Either[Nothing, SuccessModel] = Right(SuccessModel("200"))

          "return status 200" in {

            mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
            setupMockSubmitReturn("999999999", nonAgentVatReturnDetailModel, validVatReturnIdentificationModel.idType)(successResponse)
            status(result) shouldBe Status.OK
          }
        }
      }
    }
  }

}
