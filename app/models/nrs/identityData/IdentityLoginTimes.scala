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

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}

import play.api.libs.json._

case class IdentityLoginTimes(
                               currentLogin: LocalDateTime,
                               previousLogin: LocalDateTime
                             )

object IdentityLoginTimes {
  private val zoneId = ZoneId.of("UTC")
  private val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

  def parseDatesAsString(input: LocalDateTime): String = {
    input.format(
      DateTimeFormatter
        .ofPattern(dateFormat)
        .withZone(zoneId)
    )
  }

  implicit val writes: Writes[IdentityLoginTimes] = Writes[IdentityLoginTimes] { model =>
    Json.obj(
      "currentLogin" -> parseDatesAsString(model.currentLogin),
      "previousLogin" -> parseDatesAsString(model.previousLogin)
    )
  }

  private val currentLoginPath = JsPath \ "currentLogin"
  private val previousLoginPath = JsPath \ "previousLogin"

  implicit val reads: Reads[IdentityLoginTimes] = for {
    currentLoginString <- currentLoginPath.read[String]
    previousLoginString <- previousLoginPath.read[String]
  } yield {
    val currentLogin = LocalDateTime.ofInstant(Instant.parse(currentLoginString), zoneId)
    val previousLogin = LocalDateTime.ofInstant(Instant.parse(previousLoginString), zoneId)

    IdentityLoginTimes(currentLogin, previousLogin)
  }

}
