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

package connectors.httpParsers

import base.SpecBase
import connectors.httpParsers.VatReturnsHttpParser.VatReturnReads
import models._
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpResponse

class VatReturnsHttpParserSpec extends SpecBase {

  "The VatReturnsHttpParser" when {

    "the http response status is 200 OK and matches expected Schema" should {

      val testReturn: VatReturnDetail =
        VatReturnDetail(
          "18AA",
          100,
          200,
          300,
          400,
          500,
          600,
          700,
          800,
          900
        )

      val responseJson: JsValue = Json.parse(
        """{
          |"periodKey":"18AA",
          |"vatDueSales":100,
          |"vatDueAcquisitions":200,
          |"vatDueTotal":300,
          |"vatReclaimedCurrPeriod":400,
          |"vatDueNet":500,
          |"totalValueSalesExVAT":600,
          |"totalValuePurchasesExVAT":700,
          |"totalValueGoodsSuppliedExVAT":800,
          |"totalAllAcquisitionsExVAT":900
        }""".stripMargin
      )

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.OK, responseJson = Some(
        responseJson
      ))

      val expected: Either[Nothing, VatReturnDetail] = Right(testReturn)
      val result: VatReturnsHttpParser.HttpGetResult[VatReturnDetail] = VatReturnReads.read("", "", httpResponse)

      "return a VatReturn instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK but the response is not as expected" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.OK, responseJson = Some(Json.obj("invalid" -> "data")))

      val expected: Either[UnexpectedJsonFormat.type, Nothing] = Left(UnexpectedJsonFormat)

      val result: VatReturnsHttpParser.HttpGetResult[VatReturnDetail] = VatReturnReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "CODE",
          "reason" -> "ERROR MESSAGE"
        ))
      )

      val expected: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
        Status.BAD_REQUEST,
        Error(
          code = "CODE",
          reason = "ERROR MESSAGE"
        )
      ))

      val result: VatReturnsHttpParser.HttpGetResult[VatReturnDetail] = VatReturnReads.read("", "", httpResponse)

      "return a Error instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (multiple errors)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "failures" -> Json.arr(
            Json.obj(
              "code" -> "ERROR CODE 1",
              "reason" -> "ERROR MESSAGE 1"
            ),
            Json.obj(
              "code" -> "ERROR CODE 2",
              "reason" -> "ERROR MESSAGE 2"
            )
          )
        ))
      )

      val expected: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
        Status.BAD_REQUEST,
        MultiError(
          failures = Seq(
            Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
            Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
          )
        )
      ))

      val result: VatReturnsHttpParser.HttpGetResult[VatReturnDetail] = VatReturnReads.read("", "", httpResponse)

      "return a MultiError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Unexpected Json Returned)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST, responseJson = Some(Json.obj("foo" -> "bar")))

      val expected: Either[UnexpectedJsonFormat.type, Nothing] = Left(UnexpectedJsonFormat)

      val result: VatReturnsHttpParser.HttpGetResult[VatReturnDetail] = VatReturnReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (Bad Json Returned)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST, responseString = Some("Banana"))

      val expected: Either[InvalidJsonResponse.type, Nothing] = Left(InvalidJsonResponse)

      val result: VatReturnsHttpParser.HttpGetResult[VatReturnDetail] = VatReturnReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 500 Internal Server Error" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR,
        responseJson = Some(Json.obj(
          "code" -> "code",
          "reason" -> "reason"
        ))
      )

      val expected: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
        Status.INTERNAL_SERVER_ERROR,
        Error(
          code = "code",
          reason = "reason"
        )
      ))

      val result: VatReturnsHttpParser.HttpGetResult[VatReturnDetail] = VatReturnReads.read("", "", httpResponse)

      "return an Internal Server Error" in {
        result shouldEqual expected
      }
    }

    "the http response status is unexpected" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.SEE_OTHER)

      val expected: Either[UnexpectedResponse.type, Nothing] = Left(UnexpectedResponse)

      val result: VatReturnsHttpParser.HttpGetResult[VatReturnDetail] = VatReturnReads.read("", "", httpResponse)

      "return an Internal Server Error" in {
        result shouldEqual expected
      }
    }
  }
}
