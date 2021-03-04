/*
 * Copyright 2021 HM Revenue & Customs
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

import java.time.LocalDate

import models.nrs.identityData._
import play.api.libs.json.{Json, OFormat}

case class IdentityData(
                            internalId: Option[String] = None,
                            externalId: Option[String] = None,
                            agentCode: Option[String] = None,
                            credentials: Option[IdentityCredentials] = None,
                            confidenceLevel: Int,
                            nino: Option[String] = None,
                            saUtr: Option[String] = None,
                            name: Option[IdentityName] = None,
                            dateOfBirth: Option[LocalDate] = None,
                            email: Option[String] = None,
                            agentInformation: IdentityAgentInformation,
                            groupIdentifier: Option[String] = None,
                            credentialRole: Option[String] = None,
                            mdtpInformation: Option[IdentityMdtpInformation] = None,
                            itmpName: IdentityItmpName,
                            itmpDateOfBirth: Option[LocalDate] = None,
                            itmpAddress: IdentityItmpAddress,
                            affinityGroup: Option[String] = None,
                            credentialStrength: Option[String] = None,
                            loginTimes: IdentityLoginTimes
                          )

object IdentityData {
  implicit val formats: OFormat[IdentityData] = Json.format[IdentityData]
}
