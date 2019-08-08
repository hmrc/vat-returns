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

package models.nrs.identityData

import play.api.libs.json.{JsObject, Json}
import base.SpecBase

class IdentityItmpAddressSpec extends SpecBase {

  val correctJson: JsObject = Json.obj(
    "line1" -> "123 Test Lane",
    "postCode" -> "CW00F",
    "countryName" -> "England",
    "countryCode" -> "EN"
  )

  val correctModel: IdentityItmpAddress = IdentityItmpAddress(
    line1 = Some("123 Test Lane"),
    postCode = Some("CW00F"),
    countryName = Some("England"),
    countryCode = Some("EN")
  )

  "Formats" should {
    "parse correctly from json" in {
      correctJson.as[IdentityItmpAddress] shouldBe correctModel
    }
    "parse correctly to json" in {
      Json.toJson(correctModel) shouldBe correctJson
    }
  }
}
