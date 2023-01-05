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

package audit.models

import models.VatReturnDetail
import play.api.libs.json.{JsValue, Json}

// TODO: This code will need tweaking to match audit requirements from TXM or otherwise, or removed if none.
case class VatReturnResponseAuditModel(vrn: String, transaction: VatReturnDetail) extends ExtendedAuditModel {
  override val transactionName: String = "vat-returns-response"
  override val auditType: String = "vatReturnsResponse"
  override val detail: JsValue = Json.obj(
    "vrn" -> vrn,
    "response" -> Json.toJson(
      TransactionsAuditModel(
        "VRN",
        vrn
      )
    )
  )
}
