/*
 * Copyright 2018 HM Revenue & Customs
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

import binders.VatReturnsBinders
import helpers.ComponentSpecBase
import helpers.servicemocks.DesVatReturnsStub
import models._
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import testData.VatReturnData

class VatReturnsComponentSpec extends ComponentSpecBase {

  "Sending a request to /vat-returns/returns/vrn/:vrn/ (VatReturnsController)" when {

    "Requesting Vat Returns" should {

      lazy val vrn: String = "555555555"

      "be authorised with a valid request with a period key and a success response" should {

        lazy val queryParameters: VatReturnFilters = VatReturnFilters(
          periodKey = "17AA"
        )

        "return a success response" in {

          isAuthorised()

        And("When wiremock stubbing a successful Get Vat Returns Data response")
          DesVatReturnsStub.stubGetVatReturns(vrn, queryParameters)(OK,
            Json.toJson(VatReturnData.successResponse))

          When(s"Calling GET /vat-returns/returns/vrn/$vrn")
          val res: WSResponse = VatReturnsComponent.getVatReturns("555555555", queryParameters)

          DesVatReturnsStub.verifyGetVatReturns(vrn, queryParameters)

          Then("a successful response is returned with the correct transformed vat obligations")
          res should have(
            httpStatus(OK),
            jsonBodyAs[VatReturnDetail](VatReturnData.successResponse)
          )
        }
      }


//      "authorised with a valid request with a period key and an error response" should {
//
//        lazy val queryParameters: VatReturnFilters= VatReturnFilters(
//          periodKey = "17AA"
//        )
//
//        "return a single error response" in {
//
//          isAuthorised()
//
//          And("When wiremock stubbing a failure Get Vat Returns Data response")
//          DesVatReturnsStub.stubGetVatReturns(vrn, queryParameters)(BAD_REQUEST,
//            Json.toJson(VatReturnData.singleErrorResponse))
//
//          When(s"Calling GET /vat-returns/vrn/$vrn")
//          val res: WSResponse = VatReturnsComponent.getVatReturns(vrn, queryParameters)
//
//          DesVatReturnsStub.verifyGetVatReturns(vrn, queryParameters)
//
//          Then("the correct single error response is returned")
//
//          res should have(
//            httpStatus(BAD_REQUEST),
//            jsonBodyAs[Error](VatReturnData.singleErrorResponse)
//          )
//        }
//      }
//
//      "authorised with a valid request with a period key and a multi error response" should {
//
//        lazy val queryParameters: VatReturnFilters= VatReturnFilters(
//          periodKey = "17AA"
//        )
//
//        "return a multi error response model" in {
//
//          isAuthorised()
//
//          And("When wiremock stubbing a failure Get Vat Returns Data response")
//          DesVatReturnsStub.stubGetVatReturns(vrn, queryParameters)(BAD_REQUEST,
//            Json.toJson(VatReturnData.multiErrorModel))
//
//          When(s"Call GET /vat-returns/returns/vrn/$vrn/")
//          val res: WSResponse = VatReturnsComponent.getVatReturns("555555555", queryParameters)
//
//          DesVatReturnsStub.verifyGetVatReturns(vrn, queryParameters)
//
//          Then("the correct multi error response is returned")
//
//          res should have(
//            httpStatus(BAD_REQUEST),
//            jsonBodyAs[MultiError](VatReturnData.multiErrorModel)
//          )
//        }
//      }
//
//      "unauthorised" should {
//
//        "return an FORBIDDEN response" in {
//
//          isAuthorised(false)
//
//          When(s"Call GET /vat-returns/returns/vrn/$vrn/")
//          val res: WSResponse = VatReturnsComponent.getVatReturns(vrn, VatReturnFilters(
//            periodKey = "17AA"
//          ))
//
//          res should have(
//            httpStatus(FORBIDDEN)
//          )
//        }
//      }
    }

  }
}
