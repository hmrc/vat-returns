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

package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class VatReturnIdentification(idType: String, idValue: String)

object VatReturnIdentification {
  implicit val format: OFormat[VatReturnIdentification] = Json.format[VatReturnIdentification]
}

case class VatReturnDetail(periodKey: String,
                           vatDueSales: BigDecimal,
                           vatDueAcquisitions: BigDecimal,
                           vatDueTotal: BigDecimal,
                           vatReclaimedCurrPeriod: BigDecimal,
                           vatDueNet: BigDecimal,
                           totalValueSalesExVAT: BigDecimal,
                           totalValuePurchasesExVAT: BigDecimal,
                           totalValueGoodsSuppliedExVAT: BigDecimal,
                           totalAllAcquisitionsExVAT: BigDecimal)

object VatReturnDetail {
  implicit val reads: Reads[VatReturnDetail] = (
      (JsPath \ "periodKey").read[String] and
      (JsPath \ "vatDueSales").read[BigDecimal] and
      (JsPath \ "vatDueAcquisitions").read[BigDecimal] and
      (JsPath \ "vatDueTotal").read[BigDecimal] and
      (JsPath \ "vatReclaimedCurrPeriod").read[BigDecimal] and
      (JsPath \ "vatDueNet").read[BigDecimal] and
      (JsPath \ "totalValueSalesExVAT").read[BigDecimal] and
      (JsPath \ "totalValuePurchasesExVAT").read[BigDecimal] and
      (JsPath \ "totalValueGoodsSuppliedExVAT").read[BigDecimal] and
      (JsPath \ "totalAllAcquisitionsExVAT").read[BigDecimal]
    ) (VatReturnDetail.apply _)

  implicit val writes: Writes[VatReturnDetail] = new Writes[VatReturnDetail] {
    def writes(vatReturnDetail: VatReturnDetail): JsObject = Json.obj(
      "periodKey" -> vatReturnDetail.periodKey,
      "vatDueSales" -> vatReturnDetail.vatDueSales,
      "vatDueAcquisitions" -> vatReturnDetail.vatDueAcquisitions,
      "totalVatDue" -> vatReturnDetail.vatDueTotal,
      "vatReclaimedCurrPeriod" -> vatReturnDetail.vatReclaimedCurrPeriod,
      "netVatDue" -> vatReturnDetail.vatDueNet,
      "totalValueSalesExVAT" -> vatReturnDetail.totalValueSalesExVAT,
      "totalValuePurchasesExVAT" -> vatReturnDetail.totalValuePurchasesExVAT,
      "totalValueGoodsSuppliedExVAT" -> vatReturnDetail.totalValueGoodsSuppliedExVAT,
      "totalAcquisitionsExVAT" -> vatReturnDetail.totalAllAcquisitionsExVAT
    )
  }
}

object VatReturn {
  implicit val format: OFormat[VatReturn] = Json.format[VatReturn]
}

case class VatReturn(identification: VatReturnIdentification, returnDetails: VatReturnDetail)
