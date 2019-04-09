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

import java.time.LocalDateTime
import base.SpecBase
import play.api.libs.json.Json

class VatReturnSubmissionSpec extends SpecBase {

  "writes" when {

    "all fields are supplied" should {

      val model: VatReturnSubmission = VatReturnSubmission(
        periodKey = "#001",
        vatDueSales = 1234567890123.23,
        vatDueAcquisitions = -9876543210912.87,
        vatDueTotal = 1234567890112.23,
        vatReclaimedCurrPeriod = -1234567890122.23,
        vatDueNet = 2345678901.12,
        totalValueSalesExVAT = 1234567890123.00,
        totalValuePurchasesExVAT = 1234567890123.00,
        totalValueGoodsSuppliedExVAT = 1234567890123.00,
        totalAllAcquisitionsExVAT = -1234567890123.00,
        agentReferenceNumber = Some("XAIT1234567"),
        receivedAt = LocalDateTime.of(2019, 8, 14, 9, 14,12)
      )

      val expectedJson = Json.parse(
        """
          | {
          |    "periodKey": "#001",
          |    "vatDueSales": 1234567890123.23,
          |    "vatDueAcquisitions": -9876543210912.87,
          |    "vatDueTotal": 1234567890112.23,
          |    "vatReclaimedCurrPeriod": -1234567890122.23,
          |    "vatDueNet": 2345678901.12,
          |    "totalValueSalesExVAT": 1234567890123.00,
          |    "totalValuePurchasesExVAT": 1234567890123.00,
          |    "totalValueGoodsSuppliedExVAT": 1234567890123.00,
          |    "totalAllAcquisitionsExVAT": -1234567890123.00,
          |    "agentReferenceNumber": "XAIT1234567",
          |    "receivedAt": "2019-08-14T09:14:12Z"
          | }
        """.stripMargin)

      "parse to JSON" in {
        Json.toJson(model) shouldBe expectedJson
      }
    }

    "only mandatory fields are supplied" should {

      val model: VatReturnSubmission = VatReturnSubmission(
        periodKey = "#001",
        vatDueSales = 1234567890123.23,
        vatDueAcquisitions = -9876543210912.87,
        vatDueTotal = 1234567890112.23,
        vatReclaimedCurrPeriod = -1234567890122.23,
        vatDueNet = 2345678901.12,
        totalValueSalesExVAT = 1234567890123.00,
        totalValuePurchasesExVAT = 1234567890123.00,
        totalValueGoodsSuppliedExVAT = 1234567890123.00,
        totalAllAcquisitionsExVAT = -1234567890123.00,
        receivedAt = LocalDateTime.of(2019, 8, 14, 9, 14,12)
      )

      val expectedJson = Json.parse(
        """
          | {
          |    "periodKey": "#001",
          |    "vatDueSales": 1234567890123.23,
          |    "vatDueAcquisitions": -9876543210912.87,
          |    "vatDueTotal": 1234567890112.23,
          |    "vatReclaimedCurrPeriod": -1234567890122.23,
          |    "vatDueNet": 2345678901.12,
          |    "totalValueSalesExVAT": 1234567890123.00,
          |    "totalValuePurchasesExVAT": 1234567890123.00,
          |    "totalValueGoodsSuppliedExVAT": 1234567890123.00,
          |    "totalAllAcquisitionsExVAT": -1234567890123.00,
          |    "receivedAt": "2019-08-14T09:14:12Z"
          | }
        """.stripMargin)

      "parse to JSON" in {
        Json.toJson(model) shouldBe expectedJson
      }
    }
  }
}
