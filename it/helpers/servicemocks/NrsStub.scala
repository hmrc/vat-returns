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

package helpers.servicemocks

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import models.Error
import models.nrs.{AppJson, NrsReceiptSuccessModel}
import play.api.http.Status.ACCEPTED
import play.api.libs.json.Json

object NrsStub extends WireMockMethods {
  private def uri(vrn: Option[String]) = "/submission" + vrn.fold("")(vrnValue => s"/$vrnValue")

  def stubSubmissionResponse(status: Int, response: Either[Error, NrsReceiptSuccessModel], apiKey: String, vrn: Option[String] = None): StubMapping = {
    when(POST, uri(vrn), Map(
      "Content-Type" -> AppJson,
      "X-API-Key" -> apiKey
    )).thenReturn(
      status = status, body = response.fold(Json.toJson(_), Json.toJson(_))
    )
  }

  def stubTimeoutResponse(vrn: Option[String] = None): StubMapping = {
    stubFor(
      post(uri(vrn)).willReturn(
        aResponse()
          .withFixedDelay(2000)
          .withBody(Json.toJson(NrsReceiptSuccessModel("some-return")).toString())
          .withStatus(ACCEPTED)
      )
    )
  }

  def stubExceptionResponse(vrn: Option[String] = None): StubMapping = {
    stubFor(
      post(uri(vrn)).willReturn(
        aResponse()
          .withFault(Fault.MALFORMED_RESPONSE_CHUNK)
      )
    )
  }

}
