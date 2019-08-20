/*
 * Copyright 2018 HM Revenue & Customs
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

package controllers

import helpers.ComponentSpecBase
import helpers.servicemocks.{AuthStub, NrsStub}
import models.nrs._
import play.api.http.Status._
import play.api.libs.json.Json

class NRSControllerISpec extends ComponentSpecBase {

  "Posting to /nrs/submission/:vrn" when {

    "user is authorised" when {

      "request body is valid" when {

        val validJson = Json.obj(
          "payload" -> "abcdefg",
          "metadata" -> Json.obj(
            "businessId" -> "anId",
            "notableEvent" -> "anEvent",
            "payloadContentType" -> "text/html",
            "payloadSha256Checksum" -> "checksum",
            "nrSubmissionId" -> "submission id",
            "userSubmissionTimestamp" -> "2019-08-19T13:21:37.126Z",
            "identityData" -> Json.obj(
              "internalId" -> "int-id",
              "externalId" -> "ext-id",
              "agentCode" -> "agent code",
              "credentials" -> Json.obj(
                "providerId" -> "someId",
                "providerType" -> "someType"
              ),
              "confidenceLevel" -> 200,
              "nino" -> "fake nino",
              "name" -> Json.obj(
                "name" -> "First",
                "lastName" -> "Last"
              ),
              "email" -> "user@test.com",
              "agentInformation" -> Json.obj(
                "agentCode" -> "Agent Code",
                "agentFriendlyName" -> "Agent Name",
                "agentId" -> "AGNT"
              ),
              "groupIdentifier" -> "group ID",
              "credentialRole" -> "role",
              "itmpName" -> Json.obj(
                "givenName" -> "Given",
                "middleName" -> "Middle",
                "familyName" -> "Last"
              ),
              "itmpDateOfBirth" -> "1900-01-01",
              "itmpAddress" -> Json.obj(
                "line1" -> "Line 1",
                "postCode" -> "LN11NE",
                "countryName" -> "ENGLAND",
                "countryCode" -> "EN"
              ),
              "loginTimes" -> Json.obj(
                "currentLogin" -> "2019-08-19T13:21:37.126Z",
                "previousLogin" -> "2019-08-19T13:21:37.126Z"
              )
            ),
            "userAuthToken" -> "someToken",
            "headerData" -> Json.obj(
              "key" -> "value"
            ),
            "searchKeys" -> Json.obj(
              "vrn" -> "123456789",
              "periodKey" -> "18AA"
            ),
            "receiptData" -> Json.obj(
              "language" -> "en",
              "checkYourAnswersSections" -> Json.arr(
                Json.obj(
                  "title" -> "title",
                  "data" -> Json.arr(
                    Json.obj(
                      "questionId" -> "questionId",
                      "question" -> "question",
                      "answer" ->"answer"
                    )
                  )
                )
              ),
              "declaration" -> Json.obj(
                "declarationText" -> "declaration",
                "declarationName" -> "declarationName",
                "declarationConsent" -> true
              )
            )
          )
        )

        "NRS submission is successful" should {

          "return ACCEPTED" in {

            AuthStub.stubResponse()
            NrsStub.stubSubmissionResponse(ACCEPTED, Right(NrsReceiptSuccessModel("12345")), "not-a-key")

            val response = await(post("/nrs/submission/999999999")(validJson))

            response.status shouldBe 202
            response.json shouldBe Json.obj("nrSubmissionId" -> "12345")
          }
        }

        "NRS submission is unsuccessful" should {

          "return error status code" in {

          }
        }
      }

      "request body is invalid" should {

        "return BAD_REQUEST" in {

        }
      }
    }

    "user is unauthorised" should {

      "return UNAUTHORIZED" in {

      }
    }
  }
}
