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

package connectors

import java.time.LocalDateTime

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.ComponentSpecBase
import helpers.servicemocks.SubmitVatReturnStub
import models._
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class SubmitVatReturnConnectorSpec extends ComponentSpecBase  {

  private trait Test {
    def setupStubs(): StubMapping

    val model: VatReturnSubmission = VatReturnSubmission(
      periodKey = "#001",
      vatDueSales = 9999999999999.99,
      vatDueAcquisitions = -9999999999999.99,
      vatDueTotal = 0.00,
      vatReclaimedCurrPeriod = 0.00,
      vatDueNet = 0.00,
      totalValueSalesExVAT = 0.00,
      totalValuePurchasesExVAT = 0.00,
      totalValueGoodsSuppliedExVAT = 0.00,
      totalAllAcquisitionsExVAT = 0.00,
      agentReferenceNumber = Some("XAIT1234567"),
      receivedAt = LocalDateTime.of(2018, 8, 14, 12, 12, 12)
    )

    val postRequestJsonBody: JsValue = Json.parse(
      """
        |{
        |  "periodKey" : "#001",
        |  "vatDueSales" : 9999999999999.99,
        |  "vatDueAcquisitions" : -9999999999999.99,
        |  "vatDueTotal" : 0.00,
        |  "vatReclaimedCurrPeriod" : 0.00,
        |  "vatDueNet" : 0.00,
        |  "totalValueSalesExVAT" : 0.00,
        |  "totalValuePurchasesExVAT" : 0.00,
        |  "totalValueGoodsSuppliedExVAT" : 0.00,
        |  "totalAllAcquisitionsExVAT" : 0.00,
        |  "agentReferenceNumber" : "XAIT1234567",
        |  "receivedAt" : "2018-08-14T12:12:12Z"
        |}
      """.stripMargin
    )

    val connector: SubmitVatReturnConnector = app.injector.instanceOf[SubmitVatReturnConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "Calling .submitVatReturn" when {

    "response is 200" when {

      "response body is valid" should {

        "return a SuccessModel" in new Test {

          override def setupStubs(): StubMapping =
            SubmitVatReturnStub.stubSubmitVatReturn("999999999")(Status.OK, Json.parse(""" { "formBundleNumber": "12345" } """))
          setupStubs()

          private val result = await(connector.submitVatReturn("999999999", model))
          SubmitVatReturnStub.verifySubmission("999999999", postRequestJsonBody)

          result shouldBe Right(SuccessModel(formBundleNumber = "12345"))
        }
      }

      "response body is invalid" should {

        "return an InvalidJsonResponse" in new Test {

          override def setupStubs(): StubMapping =
            SubmitVatReturnStub.stubSubmitVatReturn("999999999")(Status.OK, Json.parse(""" {  } """))
          setupStubs()

          private val result = await(connector.submitVatReturn("999999999", model))
          SubmitVatReturnStub.verifySubmission("999999999", postRequestJsonBody)

          result shouldBe Left(UnexpectedJsonFormat)
        }
      }
    }

    "response is unexpected" when {

      "response body is valid for a single error" should {

        "return an ErrorResponse" in new Test {

          override def setupStubs(): StubMapping =
            SubmitVatReturnStub.stubSubmitVatReturn("999999999")(
              Status.INTERNAL_SERVER_ERROR,
              Json.parse(""" { "code" : "500", "reason" : "DES" } """)
            )
          setupStubs()

          private val result = await(connector.submitVatReturn("999999999", model))
          SubmitVatReturnStub.verifySubmission("999999999", postRequestJsonBody)

          result shouldBe Left(ErrorResponse(500, Error("500", "DES")))
        }
      }

      "response body is valid for a multiple errors" should {

        "return an ErrorResponse" in new Test {

          override def setupStubs(): StubMapping =
            SubmitVatReturnStub.stubSubmitVatReturn("999999999")(
              Status.INTERNAL_SERVER_ERROR,
              Json.parse(""" { "failures" : [ { "code" : "500", "reason" : "DES" }, { "code" : "503", "reason" : "Also DES" } ] } """)
            )
          setupStubs()

          private val result = await(connector.submitVatReturn("999999999", model))
          SubmitVatReturnStub.verifySubmission("999999999", postRequestJsonBody)

          val expectedResult = Left(ErrorResponse(500, MultiError(Seq(Error("500", "DES"), Error("503", "Also DES")))))

          result shouldBe expectedResult
        }
      }

      "response body is invalid" should {

        "return an InvalidJsonResponse" in new Test {

          override def setupStubs(): StubMapping =
            SubmitVatReturnStub.stubSubmitVatReturn("999999999")(
              Status.INTERNAL_SERVER_ERROR,
              Json.parse(""" { } """)
            )
          setupStubs()

          private val result = await(connector.submitVatReturn("999999999", model))
          SubmitVatReturnStub.verifySubmission("999999999", postRequestJsonBody)

          result shouldBe Left(UnexpectedJsonFormat)
        }
      }
    }
  }
}