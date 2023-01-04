/*
 * Copyright 2023 HM Revenue & Customs
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
import helpers.servicemocks.{AuthStub, GetVatReturnStub}
import models.MultiError
import models.Error
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import testData.VatReturnData

class VatReturnsComponentSpec extends ComponentSpecBase {

  val vrn = "555555555"

  "Sending a request to /vat-returns/returns/vrn/:vrn (VatReturnsController)" when {

    "authorised" when {

      "downstream response is successful" should {

        "return a success response" in {

          AuthStub.stubResponse(OK, AuthStub.externalId)
          GetVatReturnStub.stubResponse("555555555")(OK, Json.toJson(VatReturnData.successDesResponse))

          val response: WSResponse = get("/returns/vrn/555555555?period-key=17AA")

          response.status shouldBe OK
          response.json shouldBe VatReturnData.successResponse
        }
      }

      "downstream response is BAD_REQUEST with a single error" should {

        "return the correct error response" in {

          AuthStub.stubResponse(OK, AuthStub.externalId)
          GetVatReturnStub.stubResponse("555555555")(BAD_REQUEST, VatReturnData.singleErrorResponse.toJson)

          val response: WSResponse = get("/returns/vrn/555555555?period-key=17AA")

          response.status shouldBe BAD_REQUEST
          response.json.as[Error] shouldBe VatReturnData.singleErrorResponse
        }
      }

      "downstream response is BAD_REQUEST with a multi error" should {

        "return the correct error response" in {

          AuthStub.stubResponse(OK, AuthStub.externalId)
          GetVatReturnStub.stubResponse("555555555")(BAD_REQUEST, VatReturnData.multiErrorModel.toJson)

          val response: WSResponse = get("/returns/vrn/555555555?period-key=17AA")

          response.status shouldBe BAD_REQUEST
          response.json.as[MultiError] shouldBe VatReturnData.multiErrorModel
        }
      }
    }

    "auth returns UNAUTHORIZED" should {

      "return FORBIDDEN" in {

        AuthStub.stubResponse(UNAUTHORIZED, Json.obj())

        val response: WSResponse = get("/returns/vrn/555555555?period-key=17AA")

        response.status shouldBe FORBIDDEN
      }
    }

    "no external ID is returned from auth" should {

      "return a UNAUTHORIZED response" in {

        AuthStub.stubResponse(OK, Json.obj())

        val response: WSResponse = get("/returns/vrn/555555555?period-key=17AA")

        response.status shouldBe UNAUTHORIZED
      }
    }
  }
}

