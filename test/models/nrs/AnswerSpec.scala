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

package models.nrs

import base.SpecBase
import play.api.libs.json.Json
import utils.NrsTestData.AnswerTestData.MockJson._
import utils.NrsTestData.AnswerTestData.Models._

class AnswerSpec extends SpecBase {

  "Formats" should {
    "parse correctly from json" when {
      "parsing a single line answer" in {
        correctJsonSingleLineAnswer.as[Answer] shouldBe correctModelSingleLineAnswer
      }
      "parsing a multi-line answer" in {
        correctJsonMultiLineAnswer.as[Answer] shouldBe correctModelMultiLineAnswer
      }
    }
    "parse correctly to json" when {
      "parsing a single line answer" in {
        Json.toJson(correctModelSingleLineAnswer) shouldBe correctJsonSingleLineAnswer
      }
      "parsing a multi-line answer" in {
        Json.toJson(correctModelMultiLineAnswer) shouldBe correctJsonMultiLineAnswer
      }
    }
  }
}
