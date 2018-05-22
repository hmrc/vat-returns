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

package models

import base.SpecBase
import play.api.libs.json.{JsValue, Json}

class VatReturnDetailSpec extends SpecBase {

  val vatReturnModel: VatReturnDetail =
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

  val writeJson: JsValue = Json.parse(
    """{
      |"periodKey":"18AA",
      |"vatDueSales":100,
      |"vatDueAcquisitions":200,
      |"totalVatDue":300,
      |"vatReclaimedCurrPeriod":400,
      |"netVatDue":500,
      |"totalValueSalesExVAT":600,
      |"totalValuePurchasesExVAT":700,
      |"totalValueGoodsSuppliedExVAT":800,
      |"totalAcquisitionsExVAT":900
    }""".stripMargin
  )

  val readJson: JsValue = Json.parse(
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

  "A VAT Return" should {

    "serialize to JSON" in {
      Json.toJson(vatReturnModel) shouldBe writeJson
    }

    "deserialize to a VatReturn model" in {
      readJson.as[VatReturnDetail] shouldBe vatReturnModel
    }
  }
}
