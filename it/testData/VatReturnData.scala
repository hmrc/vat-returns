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

package testData

import models._
import play.api.libs.json.{JsValue, Json}

object VatReturnData {

  val successResponse: JsValue = Json.parse(
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

  val successDesResponse: JsValue = Json.parse(
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

  val singleErrorResponse: Error = Error("CODE", "ERROR MESSAGE")

  val multiErrorModel: MultiError = MultiError(
    failures = Seq(
      Error("CODE 1", "ERROR MESSAGE 1"),
      Error("CODE 2", "ERROR MESSAGE 2")
    )
  )
}
