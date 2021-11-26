/*
 * Copyright 2021 HM Revenue & Customs
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

package utils

import models.{VatReturn, VatReturnDetail, VatReturnIdentification}
import play.api.libs.json.{JsObject, JsValue, Json}

object SubmitVatReturnTestData {

  val nonAgentVatReturnDetailModel: VatReturnDetail = VatReturnDetail(
    periodKey = "periodKey",
    vatDueSales = BigDecimal(10000),
    vatDueAcquisitions = BigDecimal(2000),
    vatDueTotal = BigDecimal(12000),
    vatReclaimedCurrPeriod = BigDecimal(4000),
    vatDueNet = BigDecimal(8000),
    totalValueSalesExVAT = BigDecimal(250000),
    totalValuePurchasesExVAT = BigDecimal(120000),
    totalValueGoodsSuppliedExVAT = BigDecimal(160000),
    totalAllAcquisitionsExVAT = BigDecimal(25000),
    agentReferenceNumber = None
  )

  val agentVatReturnDetailModel: VatReturnDetail = VatReturnDetail(
    periodKey = "periodKey",
    vatDueSales = BigDecimal(10000),
    vatDueAcquisitions = BigDecimal(2000),
    vatDueTotal = BigDecimal(12000),
    vatReclaimedCurrPeriod = BigDecimal(4000),
    vatDueNet = BigDecimal(8000),
    totalValueSalesExVAT = BigDecimal(250000),
    totalValuePurchasesExVAT = BigDecimal(120000),
    totalValueGoodsSuppliedExVAT = BigDecimal(160000),
    totalAllAcquisitionsExVAT = BigDecimal(25000),
    agentReferenceNumber = Some("123456789")
  )

  val validVatReturnIdentificationModel: VatReturnIdentification = VatReturnIdentification(idType = "MDTP", idValue = "idValue")

}
