/*
 * Copyright 2023 HM Revenue & Customs
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

class IdentityAgentInformationSpec extends SpecBase {

  val correctJson: JsObject = Json.obj(
    "agentCode" -> "SOT7D",
    "agentFriendlyName" -> "ID Agent Name",
    "agentId" -> "SOEZ"
  )

  val correctModel = IdentityAgentInformation(
    agentCode = Some("SOT7D"),
    agentFriendlyName = Some("ID Agent Name"),
    agentId = Some("SOEZ")
  )

  "Formats" should {
    "correctly parse from json" in {
      correctJson.as[IdentityAgentInformation] shouldBe correctModel
    }
    "correctly parse to json" in {
      Json.toJson(correctModel) shouldBe correctJson
    }
  }
}
