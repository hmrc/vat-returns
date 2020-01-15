/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import play.api.libs.json.Writes
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, _}

case class VatReturnSubmission(periodKey: String,
                               vatDueSales: BigDecimal,
                               vatDueAcquisitions: BigDecimal,
                               vatDueTotal: BigDecimal,
                               vatReclaimedCurrPeriod: BigDecimal,
                               vatDueNet: BigDecimal,
                               totalValueSalesExVAT: BigDecimal,
                               totalValuePurchasesExVAT: BigDecimal,
                               totalValueGoodsSuppliedExVAT: BigDecimal,
                               totalAllAcquisitionsExVAT: BigDecimal,
                               agentReferenceNumber: Option[String] = None,
                               receivedAt: LocalDateTime)

object VatReturnSubmission {

  private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
  private val formatDate: LocalDateTime => String = _.format(dateTimeFormat)

  implicit val writes: Writes[VatReturnSubmission] = (
    (JsPath \ "periodKey").write[String] and
    (JsPath \ "vatDueSales").write[BigDecimal] and
    (JsPath \ "vatDueAcquisitions").write[BigDecimal] and
    (JsPath \ "vatDueTotal").write[BigDecimal] and
    (JsPath \ "vatReclaimedCurrPeriod").write[BigDecimal] and
    (JsPath \ "vatDueNet").write[BigDecimal] and
    (JsPath \ "totalValueSalesExVAT").write[BigDecimal] and
    (JsPath \ "totalValuePurchasesExVAT").write[BigDecimal] and
    (JsPath \ "totalValueGoodsSuppliedExVAT").write[BigDecimal] and
    (JsPath \ "totalAllAcquisitionsExVAT").write[BigDecimal] and
    (JsPath \ "agentReferenceNumber").writeNullable[String] and
    (JsPath \ "receivedAt").write[String].contramap(formatDate)
  )(unlift(VatReturnSubmission.unapply))

}
