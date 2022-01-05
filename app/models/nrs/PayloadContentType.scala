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

case class PayloadContentType(contentType: String) {
  override def toString: String = {
    contentType
  }
}

object AppJson extends PayloadContentType("application/json")
object AppXml extends PayloadContentType("application/xml")
object TextHtml extends PayloadContentType("text/html")

object PayloadContentType {
  implicit def asString: PayloadContentType => String = payload => payload.contentType

  implicit val writes: Writes[PayloadContentType] = Writes { payloadContentType =>
    Json.toJson(
      payloadContentType.contentType
    )
  }

  implicit val payloadRead: Reads[PayloadContentType] = for {
    payloadTypeString <- JsPath.read[String]
  } yield {
    payloadTypeString match {
      case AppJson.contentType => AppJson
      case AppXml.contentType => AppXml
      case TextHtml.contentType => TextHtml
    }
  }
}
