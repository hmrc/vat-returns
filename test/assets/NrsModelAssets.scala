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

package assets

import java.time.{LocalDateTime, Month}
import java.time.format.DateTimeFormatter

import models.nrs.identityData._
import models.nrs.{IdentityData, Metadata, PayloadContentType, SearchKeys}

object NrsModelAssets {

  val payload = "1234567890"

  val businessId = "vat"
  val notableEvent = "vat-return"
  val payloadContentType = PayloadContentType("text/html")
  val nrSubmissionId = "2dd537bc-4244-4ebf-bac9-96321be13cdc"

  val genericTime: LocalDateTime = LocalDateTime.of(1986, Month.APRIL, 8, 12, 30)
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm")

  val credentials = IdentityCredentials("providerId", "providerType")
  val name = IdentityName(Some("Test"), Some("Name"))
  val identityAgentInfo = IdentityAgentInformation(
    agentCode = Some("TZRXXV"), agentFriendlyName = Some("Agent Name"), agentId = Some("BDGL"))
  val itmpName = IdentityItmpName(givenName = Some("Bob"), middleName = Some("Middle Name"), familyName = Some("Last Name"))
  val itmpAddress = IdentityItmpAddress(line1 = Some("Burglarton"), postCode = Some("RC1 0ON"), countryName = Some("USA"), countryCode = Some("US"))
  val loginTimes = IdentityLoginTimes(genericTime, Some(genericTime))

  val identityData: IdentityData = IdentityData(
    None,
    None,
    None,
    Some(credentials),
    50,
    None,
    None,
    Some(name),
    None,
    None,
    identityAgentInfo,
    None,
    None,
    None,
    itmpName,
    None,
    itmpAddress,
    None,
    None,
    loginTimes
  )

  val userAuthToken = "98653566"
  val headerData: Map[String, String] = Map("" -> "")
  val searchKeys = SearchKeys("", "")

  val metadataModel = Metadata(
    businessId,
    notableEvent,
    payloadContentType,
    None,
    None,
    genericTime,
    identityData,
    userAuthToken,
    headerData,
    searchKeys,
    None)

}
