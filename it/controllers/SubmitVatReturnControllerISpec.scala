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

package controllers

import helpers.ComponentSpecBase
import helpers.servicemocks.AuthStub._
import helpers.servicemocks.{AuthStub, SubmitVatReturnStub}
import models.InvalidJsonResponse
import play.api.http.Status._
import play.api.libs.json.{JsObject, JsValue, Json}

class SubmitVatReturnControllerISpec extends ComponentSpecBase {

  val vrn: String = "101202303"

  val headers = Map("OriginatorID" -> "VATUI")

  val validJson: JsObject = Json.obj(
    "periodKey" -> "17AA",
    "vatDueSales" -> 1000,
    "vatDueAcquisitions" -> 1000,
    "vatDueTotal" -> 1000,
    "vatReclaimedCurrPeriod" -> 1000,
    "vatDueNet" -> 1000,
    "totalValueSalesExVAT" -> 1000,
    "totalValuePurchasesExVAT" -> 1000,
    "totalValueGoodsSuppliedExVAT" -> 1000,
    "totalAllAcquisitionsExVAT" -> 1000,
    "agentReferenceNumber" -> "lolWutsAReferenceNumber"
  )

  val invalidJson: JsObject = Json.obj(
    "periodKey" -> "17AA",
    "vatDueSales" -> 1000,
    "vatDueAcquisitions" -> "WOAH THIS IS SUPPOSED TO BE A NUMBER"
  )

  val successReturnBody: JsValue = Json.obj("formBundleNumber" -> "ASDFGHJKL")
  val errorReturnBody: String => JsValue = someValue => Json.obj("code" -> someValue, "reason" -> "this doesn't really matter now, does it?")

  "Posting to /vat-returns/returns/vrn/:vrn" when {

    "user is authorised" when {

      "return a success" when {

        "the service layer returns a successful response" in {

          AuthStub.stubResponse()
          SubmitVatReturnStub.stubResponse("999999999")(OK, successReturnBody)

          val response = await(post("/returns/vrn/999999999", headers)(validJson))

          SubmitVatReturnStub.verifySubmissionHeaders("999999999")
          response.status shouldBe 200
          response.json shouldBe successReturnBody
        }
      }

      "return an error" when {

        "the service layer returns an error" when {

          "the error is a 400" in {

            AuthStub.stubResponse()
            SubmitVatReturnStub.stubResponse("999999999")(BAD_REQUEST, errorReturnBody("REEEEEEEEE"))

            val response = await(post("/returns/vrn/999999999", headers)(validJson))

            response.status shouldBe BAD_REQUEST
          }

          "the error is a 404" in {

            AuthStub.stubResponse()
            SubmitVatReturnStub.stubResponse("999999999")(NOT_FOUND, errorReturnBody("REEEE"))

            val response = await(post("/returns/vrn/999999999", headers)(validJson))

            response.status shouldBe NOT_FOUND
          }

          "the error is a 500" in {

            AuthStub.stubResponse()
            SubmitVatReturnStub.stubResponse("999999999")(INTERNAL_SERVER_ERROR, errorReturnBody("REEEE"))

            val response = await(post("/returns/vrn/999999999", headers)(validJson))

            response.status shouldBe INTERNAL_SERVER_ERROR
          }

          "the error is a 503" in {

            AuthStub.stubResponse()
            SubmitVatReturnStub.stubResponse("999999999")(SERVICE_UNAVAILABLE, errorReturnBody("REEEE"))

            val response = await(post("/returns/vrn/999999999", headers)(validJson))

            response.status shouldBe SERVICE_UNAVAILABLE
          }
        }

        "the json cannot be parsed" in {

          AuthStub.stubResponse()
          val response = await(post("/returns/vrn/999999999")(invalidJson))

          response.status shouldBe INTERNAL_SERVER_ERROR
          response.json shouldBe InvalidJsonResponse.toJson
        }

        "request does not have an OriginatorID in header" in {

          AuthStub.stubResponse()
          val response = await(post("/returns/vrn/999999999")(validJson))

          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.obj("code" -> "400", "reason" -> "No OriginatorID found in header")
        }

        "OriginatorID in header is invalid" in {

          AuthStub.stubResponse()
          val response = await(post("/returns/vrn/999999999", Map("OriginatorID" -> "Another channel"))(validJson))

          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.obj("code" -> "400", "reason" -> "Invalid OriginatorID header value")
        }
      }
    }

    "requested VRN does not match auth VRN" should {

      "return FORBIDDEN" in {

        AuthStub.stubResponse()

        val response = await(post("/returns/vrn/123123123")(validJson))

        response.status shouldBe FORBIDDEN
      }
    }

    "user does not have HMRC-MTD-VAT enrolment" should {

      "return FORBIDDEN" in {

        AuthStub.stubResponse(OK, authResponse(otherEnrolment))

        val response = await(post("/returns/vrn/999999999")(validJson))

        response.status shouldBe FORBIDDEN
      }
    }
  }
}
