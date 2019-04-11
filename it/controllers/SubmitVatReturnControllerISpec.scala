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

import akka.stream.Materializer
import helpers.ComponentSpecBase
import helpers.WiremockHelper.stubPost
import models.{InvalidJsonResponse, SuccessModel, VatReturnDetail}
import play.api.http.Status._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc._
import play.api.test.FakeRequest
import services.VatReturnsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class SubmitVatReturnControllerISpec extends ComponentSpecBase {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val mat: Materializer = app.injector.instanceOf[Materializer]

  val vrn: String = "101202303"
  val service: VatReturnsService = app.injector.instanceOf[VatReturnsService]
  val controller = new SubmitVatReturnController(service)

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

  val positiveFakeRequest: FakeRequest[AnyContent] = FakeRequest(
    "POST", "", Headers(), AnyContentAsJson(validJson)
  )
  val negativeFakeRequest: FakeRequest[AnyContent] = FakeRequest(
    "POST", "", Headers(), AnyContentAsJson(invalidJson)
  )

  "submitVatReturn" should {
    "return a success" when {
      "the service layer returns a successful response" in {
        stubPost("/enterprise/return/vat/101202303", OK, Json.stringify(successReturnBody))

        val response = await(controller.submitVatReturn("101202303").apply(positiveFakeRequest))

        status(response) shouldBe 200
        val bodyReturn: String = bodyOf(response)
        Json.parse(bodyReturn) shouldBe successReturnBody
      }
    }
    "return an error" when {
      "the service layer returns an error" when {
        "the error is a 400" in {
          stubPost("/enterprise/return/vat/101202303", BAD_REQUEST, Json.stringify(errorReturnBody("REEEEEEEEE")))

          val response = controller.submitVatReturn("101202303").apply(positiveFakeRequest)

          status(response) shouldBe BAD_REQUEST
        }
        "the error is a 404" in {
          stubPost("/enterprise/return/vat/101202303", NOT_FOUND, Json.stringify(errorReturnBody("PLEASE GIMME CHOCOLATE")))

          val response = controller.submitVatReturn("101202303").apply(positiveFakeRequest)

          status(response) shouldBe NOT_FOUND
        }
        "the error is a 500" in {
          stubPost("/enterprise/return/vat/101202303", INTERNAL_SERVER_ERROR, Json.stringify(errorReturnBody("I'M A PRETTY FLOWER")))

          val response = controller.submitVatReturn("101202303").apply(positiveFakeRequest)

          status(response) shouldBe INTERNAL_SERVER_ERROR
        }
        "the error is a 503" in {
          stubPost("/enterprise/return/vat/101202303", SERVICE_UNAVAILABLE, Json.stringify(errorReturnBody("WHY WON'T YOU LOVE ME?!")))

          val response = controller.submitVatReturn("101202303").apply(positiveFakeRequest)

          status(response) shouldBe SERVICE_UNAVAILABLE
        }
      }
      "the json cannot be parsed" in {
        val response = await(controller.submitVatReturn("101202303").apply(negativeFakeRequest))

        status(response) shouldBe INTERNAL_SERVER_ERROR
        Json.parse(bodyOf(response)) shouldBe InvalidJsonResponse.toJson
      }
    }
  }
}
