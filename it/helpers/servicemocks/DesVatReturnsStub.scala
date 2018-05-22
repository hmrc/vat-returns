/*
 * Copyright 2018 HM Revenue & Customs
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

package helpers.servicemocks

import binders.VatReturnsBinders
import helpers.WiremockHelper._
import models.VatReturnFilters
import play.api.libs.json.JsValue
import com.github.tomakehurst.wiremock.stubbing.StubMapping

object DesVatReturnsStub {

  private def vatReturnsUrl(vrn: String, queryParameters: VatReturnFilters): String = {
    s"/vat-returns/returns/vrn/$vrn" +
      s"?${VatReturnsBinders.vatReturnsQueryBinder.unbind("", queryParameters)}"
  }

  def stubGetVatReturns(vrn: String, queryParams: VatReturnFilters)(status: Int, response: JsValue): StubMapping =
    stubGet(vatReturnsUrl(vrn, queryParams), status, response.toString())

  def verifyGetVatReturns(vrn: String, queryParams: VatReturnFilters): Unit =
    verifyGet(vatReturnsUrl(vrn, queryParams))
}
