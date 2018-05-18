/*
 * Copyright 2017 HM Revenue & Customs
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

object VatReturnData {

  val successResponse: VatReturnDetail =
    VatReturnDetail(
      "17AA",
      1.23,
      1.23,
      1.23,
      1.23,
      1.23,
      1.23,
      1.23,
      1.23,
      1.23
    )

  val singleErrorResponse: Error = Error("CODE", "ERROR MESSAGE")

  val multiErrorModel: MultiError = MultiError(
    failures = Seq(
      Error("CODE 1", "ERROR MESSAGE 1"),
      Error("CODE 2", "ERROR MESSAGE 2")
    )
  )
}
