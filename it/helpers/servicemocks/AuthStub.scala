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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.auth.core.AffinityGroup.Individual

object AuthStub extends WireMockMethods {

  val authoriseUrl: String = "/auth/authorise"

  def stubResponse(status: Int = OK, body: JsObject = authResponse(mtdVatEnrolment)): StubMapping = {
    when(method = POST, uri = authoriseUrl)
      .thenReturn(status = status, body = body)
  }

  def authResponse(enrolments: JsObject*): JsObject = {
    Json.obj(
      "affinityGroup" -> Individual,
      "allEnrolments" -> enrolments
    )
  }

  val mtdVatEnrolment = Json.obj(
    "key" -> "HMRC-MTD-VAT",
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> "VRN",
        "value" -> "999999999"
      )
    )
  )

  val externalId = Json.obj(
    "externalId" -> "1234"
  )

  val otherEnrolment: JsObject = Json.obj(
    "key" -> "HMRC-XXX-XXX",
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> "XXX",
        "value" -> "XXX"
      )
    )
  )
}
