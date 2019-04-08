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
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global

class SubmitVatReturnConnectorSpec extends ComponentSpecBase  {

  private trait Test {
    def setupStubs(): StubMapping
    val model: VatReturnSubmission = VatReturnSubmission(
      periodKey = "#001",
      vatDueSales = 1234567890123.23,
      vatDueAcquisitions = -9876543210912.87,
      vatDueTotal = 1234567890112.23,
      vatReclaimedCurrPeriod = -1234567890122.23,
      vatDueNet = 2345678901.12,
      totalValueSalesExVAT = 1234567890123.00,
      totalValuePurchasesExVAT = 1234567890123.00,
      totalValueGoodsSuppliedExVAT = 1234567890123.00,
      totalAllAcquisitionsExVAT = -1234567890123.00,
      receivedAt = LocalDateTime.now()
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
          val expectedResult = Right(SuccessModel(formBundleNumber = "12345"))

          result shouldBe expectedResult
        }
      }

      "response body is invalid" should {

        "return an InvalidJsonResponse" in new Test {

          override def setupStubs(): StubMapping =
          SubmitVatReturnStub.stubSubmitVatReturn("999999999")(Status.OK, Json.parse(""" {  } """))

          setupStubs()
          private val result = await(connector.submitVatReturn("999999999", model))
          val expectedResult = Left(UnexpectedJsonFormat)

          result shouldBe expectedResult
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
          val expectedResult = Left(ErrorResponse(500, Error("500", "DES")))

          result shouldBe expectedResult
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
          val expectedResult = Left(UnexpectedJsonFormat)

          result shouldBe expectedResult
        }
      }
    }
  }
}

object SubmitVatReturnConnectorSpec {

}
