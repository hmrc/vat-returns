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

package models.nrs.identityData

import base.SpecBase
import play.api.libs.json.{JsObject, Json}

class IdentityNameSpec extends SpecBase {

  val correctJson: JsObject = Json.obj(
    "name" -> "First",
    "lastName" -> "Last"
  )

  val correctModel = IdentityName(name = Some("First"), lastName = Some("Last"))

  "Formats" should {
    "correctly parse from json" in {
      correctJson.as[IdentityName] shouldBe correctModel
    }
    "correctly parse to json" in {
      Json.toJson(correctModel) shouldBe correctJson
    }
  }
}
