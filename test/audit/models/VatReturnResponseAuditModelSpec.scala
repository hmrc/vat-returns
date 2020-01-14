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

package audit.models

import base.SpecBase
import models.VatReturnDetail
import play.api.libs.json.{JsValue, Json}

class VatReturnResponseAuditModelSpec extends SpecBase {

  val transactionName = "vat-returns-response"
  val auditEvent = "vatReturnsResponse"
  val testVrn = "999999999"

  "The VatReturnsResponseAuditModel" should {

    val testTransaction: VatReturnDetail = VatReturnDetail(
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

    object TestVatReturnResponseAuditModel extends VatReturnResponseAuditModel(testVrn, testTransaction)

    s"have the correct transaction name of '$transactionName'" in {
      TestVatReturnResponseAuditModel.transactionName shouldBe transactionName
    }

    s"have the correct audit event type of '$auditEvent'" in {
      TestVatReturnResponseAuditModel.auditType shouldBe auditEvent
    }

    "have the correct details for the audit event" in {
      val expected: JsValue = Json.obj(
        "vrn" -> testVrn,
        "response" -> Json.toJson(
          TransactionsAuditModel(
            idType = "VRN",
            idValue = testVrn
          ))
      )

      TestVatReturnResponseAuditModel.detail shouldBe expected
    }
  }
}
