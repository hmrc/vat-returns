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

package models.nrs

import java.time.LocalDate

import models.nrs.identityData._
import play.api.libs.json.{Json, OFormat}

case class NrsIdentityData(
                            internalId: String,
                            externalId: String,
                            agentCode: String,
                            credentials: IdentityCredentials,
                            confidenceLevel: Int,
                            nino: String,
                            saUtr: String,
                            name: IdentityName,
                            dateOfBirth: LocalDate,
                            email: String,
                            agentInformation: IdentityAgentInformation,
                            groupIdentifier: String,
                            credentialRole: String,
                            mdtpInformation: IdentityMdtpInformation,
                            itmpName: IdentityItmpName,
                            itmpDateOfBirth: LocalDate,
                            itmpAddress: IdentityItmpAddress,
                            affinityGroup: String,
                            credentialStrength: String,
                            loginTimes: IdentityLoginTimes
                          )

object NrsIdentityData {
  implicit val formats: OFormat[NrsIdentityData] = Json.format[NrsIdentityData]
}
