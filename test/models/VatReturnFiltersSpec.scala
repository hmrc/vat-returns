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

import base.SpecBase
import VatReturnFilters._

class VatReturnFiltersSpec extends SpecBase {

  "VatReturnFilters" should {

    "have the correct key value for 'periodKey'" in {
      periodKeyValue shouldBe "period-key"
    }
  }

  "The .toSeqQueryParams function" should {

    "output a correct period-key value" in {
      val queryParams: VatReturnFilters = VatReturnFilters(periodKey = "18AA")
      queryParams.toSeqQueryParams shouldBe Seq(periodKeyValue -> "18AA")
    }
  }
}
