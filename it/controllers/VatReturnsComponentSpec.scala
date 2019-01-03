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

import helpers.ComponentSpecBase
import helpers.servicemocks.{AuthStub, DesVatReturnsStub}
import models._
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import testData.VatReturnData

class VatReturnsComponentSpec extends ComponentSpecBase {

  val vrn = "555555555"

  def getStubResponse(responseStatus: Integer, responseJson: JsValue, authorised: Boolean = true): WSResponse = {
    if (authorised) AuthStub.stubAuthorised() else AuthStub.stubUnauthorised()

    lazy val queryParameters: VatReturnFilters = VatReturnFilters(periodKey = "17AA")
    DesVatReturnsStub.stubGetVatReturns(vrn, queryParameters)(responseStatus, responseJson)
    val response = VatReturnsComponent.getVatReturns(vrn, queryParameters)
    DesVatReturnsStub.verifyGetVatReturns(vrn, queryParameters)

    response
  }

  "Sending a request to /vat-returns/returns/vrn/:vrn/ (VatReturnsController)" when {

    "Requesting Vat Returns" should {

      "be authorised with a valid request with a period key and a success response" should {

        "return a success response" in {
          val response = getStubResponse(OK, Json.toJson(VatReturnData.successResponse))

          response.status shouldBe OK
        }

        "return the expected json" in {
          val response = getStubResponse(OK, Json.toJson(VatReturnData.successResponse))

          response.json shouldBe VatReturnData.successResponse
        }
      }


      "authorised with a valid request with a period key and an error response" should {

        "return a single error response" in {
          val response = getStubResponse(BAD_REQUEST, Json.toJson(VatReturnData.singleErrorResponse))

          response.status shouldBe BAD_REQUEST
        }

        "return the expected json" in {
          val response = getStubResponse(BAD_REQUEST, Json.toJson(VatReturnData.singleErrorResponse))

          response.json.as[Error] shouldBe VatReturnData.singleErrorResponse
        }
      }

      "authorised with a valid request with a period key and a multi error response" should {

        "return a BAD_REQUEST status" in {
          val response = getStubResponse(BAD_REQUEST, Json.toJson(VatReturnData.multiErrorModel))

          response.status shouldBe BAD_REQUEST
        }

        "return the expected json" in {
          val response = getStubResponse(BAD_REQUEST, Json.toJson(VatReturnData.multiErrorModel))

          response.json.as[MultiError] shouldBe VatReturnData.multiErrorModel
        }
      }

      "unauthorised" should {

        "return an FORBIDDEN response" in {
          val response = getStubResponse(FORBIDDEN, Json.toJson(VatReturnData.singleErrorResponse), authorised = false)

          response.status shouldBe FORBIDDEN
        }
      }
    }
  }
}

