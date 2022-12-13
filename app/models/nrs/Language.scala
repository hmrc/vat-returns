/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json._

case class Language(languageCode: String)

object EN extends Language("en")
object CY extends Language("cy")

object Language {
  implicit val reads: Reads[Language] = for {
    languageString <- JsPath.read[String]
  } yield {
    languageString match {
      case CY.languageCode => CY
      case _ => EN
    }
  }

  implicit val writes: Writes[Language] = Writes { model =>
    Json.toJson(model.languageCode)
  }
}
