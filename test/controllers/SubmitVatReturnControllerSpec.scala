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

package controllers

import base.SpecBase
import controllers.actions.AuthorisedSubmitVatReturn
import mocks.auth.{MockAuthConnector, MockMicroserviceAuthorisedFunctions}
import mocks.services.MockVatReturnsService
import models.SuccessModel
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments, MissingBearerToken}
import utils.SubmitVatReturnTestData._

import scala.concurrent.Future

class SubmitVatReturnControllerSpec extends SpecBase with MockVatReturnsService with MockMicroserviceAuthorisedFunctions with MockAuthConnector {

  val authorisedSubmitVatReturn = new AuthorisedSubmitVatReturn(mockAuthConnector, controllerComponents)

  object TestSubmitVatReturnController extends SubmitVatReturnController(mockVatReturnsService, authorisedSubmitVatReturn, controllerComponents)

  val enrolments = Enrolments(Set(Enrolment("HMRC-MTD-VAT").withIdentifier("VRN", "999999999")))

  "Calling the .submitVatReturn method as a user" which {

    "is an authenticated user" when {

      "there is a non-empty request" which {

        "has a valid originator ID" when {

          "the user is a non-agent" should {

            lazy val result = TestSubmitVatReturnController.submitVatReturn("999999999")(FakeRequest().withJsonBody(
              nonAgentVatReturnDetailReadJson).withHeaders("OriginatorID" -> "MDTP")
            )
            val successResponse: Either[Nothing, SuccessModel] = Right(SuccessModel("200"))

            "return status 200" in {

              mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
              setupMockSubmitReturn("999999999", nonAgentVatReturnDetailModel, validVatReturnIdentificationModel.idType)(successResponse)
              status(result) shouldBe Status.OK
            }
          }

          "the user is an agent" should {

            lazy val result = TestSubmitVatReturnController.submitVatReturn("999999999")(FakeRequest().withJsonBody(
              agentVatReturnDetailReadJson).withHeaders("OriginatorID" -> "MDTP")
            )
            val successResponse: Either[Nothing, SuccessModel] = Right(SuccessModel("200"))

            "return status 200" in {

              mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
              setupMockSubmitReturn("999999999", agentVatReturnDetailModel, validVatReturnIdentificationModel.idType)(successResponse)
              status(result) shouldBe Status.OK
            }
          }
        }

        "has an invalid originator ID" should {

          lazy val result = TestSubmitVatReturnController.submitVatReturn("999999999")(FakeRequest().withJsonBody(
            nonAgentVatReturnDetailReadJson).withHeaders("OriginatorID" -> "Something invalid")
          )

          "return status 400" in {

            mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
            status(result) shouldBe Status.BAD_REQUEST
          }
        }

        "the user has no originator ID" should {

          lazy val result = TestSubmitVatReturnController.submitVatReturn("999999999")(FakeRequest().withJsonBody(
            nonAgentVatReturnDetailReadJson)
          )

          "return status 400" in {

            mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
            status(result) shouldBe Status.BAD_REQUEST
          }
        }
      }

      "the request is empty" should {

        lazy val result = TestSubmitVatReturnController.submitVatReturn("999999999")(FakeRequest())

        "return status 500" in {

          mockAuthorise()(Future.successful(new ~(Some(AffinityGroup.Individual), enrolments)))
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR

        }
      }
    }

    "is not authenticated" should {

      lazy val result = TestSubmitVatReturnController.submitVatReturn("999999999")(FakeRequest().withJsonBody(
        nonAgentVatReturnDetailReadJson).withHeaders("OriginatorID" -> "MDTP"))


      "return UNAUTHORIZED status" in {

        mockAuthorise()(Future.failed(MissingBearerToken()))
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }
  }
}
