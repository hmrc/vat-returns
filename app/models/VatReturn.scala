/*
 * Copyright 2022 HM Revenue & Customs
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
                           totalAllAcquisitionsExVAT: BigDecimal,
                           agentReferenceNumber: Option[String] = None)

object VatReturnDetail {

  implicit val reads: Reads[VatReturnDetail] = Json.reads[VatReturnDetail]

  implicit val writes: Writes[VatReturnDetail] = (
    (JsPath \ "periodKey").write[String] and
    (JsPath \ "vatDueSales").write[BigDecimal] and
    (JsPath \ "vatDueAcquisitions").write[BigDecimal] and
    (JsPath \ "totalVatDue").write[BigDecimal] and
    (JsPath \ "vatReclaimedCurrPeriod").write[BigDecimal] and
    (JsPath \ "netVatDue").write[BigDecimal] and
    (JsPath \ "totalValueSalesExVAT").write[BigDecimal] and
    (JsPath \ "totalValuePurchasesExVAT").write[BigDecimal] and
    (JsPath \ "totalValueGoodsSuppliedExVAT").write[BigDecimal] and
    (JsPath \ "totalAcquisitionsExVAT").write[BigDecimal] and
    (JsPath \ "agentReferenceNumber").writeNullable[String]
  )(unlift(VatReturnDetail.unapply))
}

object VatReturn {
  implicit val format: OFormat[VatReturn] = Json.format[VatReturn]
}

case class VatReturn(identification: VatReturnIdentification, returnDetails: VatReturnDetail)
