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

package audit.models

import base.SpecBase
import models.VatReturnFilters
import models.VatReturnFilters.periodKeyValue

class VatReturnRequestAuditModelSpec extends SpecBase {

  val transactionName = "vat-returns-request"
  val auditEvent = "vatReturnsRequest"
  val testVrn = "999999999"

  "The VatReturnRequestAuditModel" should {

    val testQueryParam: VatReturnFilters = VatReturnFilters(periodKey = "18AA")
    object TestVatReturnRequestAuditModel extends VatReturnRequestAuditModel(testVrn, testQueryParam)

    s"have the correct transaction name of '$transactionName'" in {
      TestVatReturnRequestAuditModel.transactionName shouldBe transactionName
    }

    s"have the correct audit event type of '$auditEvent'" in {
      TestVatReturnRequestAuditModel.auditType shouldBe auditEvent
    }

    "have the correct details for the audit event" in {
      TestVatReturnRequestAuditModel.detail shouldBe Seq(
        "vrn" -> testVrn,
        periodKeyValue -> testQueryParam.periodKey
      )
    }
  }
}
