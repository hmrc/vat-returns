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

package utils

import java.time.{Instant, LocalDate, LocalDateTime, ZoneId}

import models.nrs.identityData._
import models.nrs._
import play.api.libs.json.{JsObject, JsValue, Json}

object NrsTestData {


  object AnswerTestData {

    object MockJson {
      val correctJsonSingleLineAnswer: JsObject = Json.obj(
        "questionId" -> "00192IIO",
        "question" -> "What is your name?",
        "answer" -> "Test User"
      )

      val correctJsonMultiLineAnswer: JsObject = Json.obj(
        "questionId" -> "00192IID",
        "question" -> "Where are you from?",
        "answers" -> Json.arr(
          "Hang on, let me just think",
          "Oh, I'm from Test Lane"
        )
      )
    }

    object Models {
      val correctModelSingleLineAnswer: Answer = Answer(
        questionId = "00192IIO",
        question = "What is your name?",
        answer = Some("Test User")
      )

      val correctModelMultiLineAnswer: Answer = Answer(
        questionId = "00192IID",
        question = "Where are you from?",
        answer = None,
        answers = Some(Seq(
          "Hang on, let me just think",
          "Oh, I'm from Test Lane"
        ))
      )
    }

  }

  object AnswersTestData {
    val correctJson: JsObject = Json.obj(
      "title" -> "Test Title",
      "data" -> Json.arr(
        AnswerTestData.MockJson.correctJsonSingleLineAnswer,
        Json.obj(
          "questionId" -> "00192IIP",
          "question" -> "Who are you?",
          "answer" -> "Test User 2"
        )
      )
    )

    val correctModel: Answers = Answers(
      title = "Test Title",
      data = Seq(
        AnswerTestData.Models.correctModelSingleLineAnswer,
        Answer(
          questionId = "00192IIP",
          question = "Who are you?",
          answer = Some("Test User 2")
        )
      )
    )
  }

  object DeclarationTestData {
    val correctJson: JsValue = Json.parse(
      """
        |{
        | "declarationText": "I confirm the data...",
        | "declarationName": "Test User",
        | "declarationRole": "Financial Director",
        | "declarationConsent": true
        |}
      """.stripMargin)

    val correctModel: Declaration = Declaration(
      declarationText = "I confirm the data...",
      declarationName = "Test User",
      declarationRole = Some("Financial Director"),
      declarationConsent = true
    )
  }

  object ReceiptTestData {
    val correctJson: JsObject = Json.obj(
      "language" -> "en",
      "checkYourAnswersSections" -> Json.arr(
        AnswersTestData.correctJson
      ),
      "declaration" -> DeclarationTestData.correctJson
    )

    val correctModel = ReceiptData(
      EN, Seq(AnswersTestData.correctModel), DeclarationTestData.correctModel
    )
  }

  object IdentityDataTestData {


    val correctJson: JsValue = Json.parse(
      """{
        |  "internalId": "some-id",
        |  "externalId": "some-id",
        |  "agentCode": "TZRXXV",
        |  "credentials": {"providerId": "12345-credId",
        |  "providerType": "GovernmentGateway"},
        |  "confidenceLevel": 200,
        |  "nino": "DH00475D",
        |  "saUtr": "Utr",
        |  "name": { "name": "test", "lastName": "test" },
        |  "dateOfBirth": "1985-01-01",
        |  "email":"test@test.com",
        |  "agentInformation": {
        |    "agentCode" : "TZRXXV",
        |    "agentFriendlyName" : "Bodgitt & Legget LLP",
        |    "agentId": "BDGL"
        |  },
        |  "groupIdentifier" : "GroupId",
        |  "credentialRole": "admin",
        |  "mdtpInformation" : {"deviceId" : "DeviceId",
        |    "sessionId": "SessionId" },
        |  "itmpName" : { "givenName": "test",
        |    "middleName": "test", "familyName": "test" },
        |  "itmpDateOfBirth" : "1985-01-01",
        |  "itmpAddress" : {
        |    "line1": "Line 1",
        |    "postCode": "NW94HD",
        |    "countryName": "United Kingdom",
        |    "countryCode": "UK"
        |    },
        |  "affinityGroup": "Agent",
        |  "credentialStrength": "strong",
        |  "loginTimes": {
        |    "currentLogin": "2016-11-27T09:00:00.000Z",
        |    "previousLogin": "2016-11-01T12:00:00.000Z"
        |  }
        |}""".stripMargin)

    val correctModel: IdentityData = IdentityData(
      internalId = Some("some-id"),
      externalId = Some("some-id"),
      agentCode = Some("TZRXXV"),
      credentials = IdentityCredentials(
        providerId = "12345-credId",
        providerType = "GovernmentGateway"
      ),
      confidenceLevel = 200,
      nino = Some("DH00475D"),
      saUtr = Some("Utr"),
      name = IdentityName(
        name = Some("test"),
        lastName = Some("test")
      ),
      dateOfBirth = Some(LocalDate.parse("1985-01-01")),
      email = Some("test@test.com"),
      agentInformation = IdentityAgentInformation(
        agentCode = Some("TZRXXV"),
        agentFriendlyName = Some("Bodgitt & Legget LLP"),
        agentId = Some("BDGL")
      ),
      groupIdentifier = Some("GroupId"),
      credentialRole = Some("admin"),
      mdtpInformation = Some(
        IdentityMdtpInformation(
          deviceId = "DeviceId",
          sessionId = "SessionId")
      ),
      itmpName = IdentityItmpName(
        givenName = Some("test"),
        middleName = Some("test"),
        familyName = Some("test")
      ),
      itmpDateOfBirth = Some(LocalDate.parse("1985-01-01")),
      itmpAddress = IdentityItmpAddress(
        line1 = Some("Line 1"),
        postCode = Some("NW94HD"),
        countryName = Some("United Kingdom"),
        countryCode = Some("UK")
      ),
      affinityGroup = Some("Agent"),
      credentialStrength = Some("strong"),
      loginTimes = IdentityLoginTimes(
        LocalDateTime.ofInstant(Instant.parse("2016-11-27T09:00:00.000Z"), ZoneId.of("UTC")),
        Some(LocalDateTime.ofInstant(Instant.parse("2016-11-01T12:00:00.000Z"), ZoneId.of("UTC")))
      )
    )
  }

  object MetadataTestData {
    val correctJson: JsValue = Json.parse(
      s"""
         |{
         |    "businessId": "vat",
         |    "notableEvent": "vat-registration",
         |    "payloadContentType": "text/html",
         |    "payloadSha256Checksum": "426a1c28<snip>d6d363",
         |    "userSubmissionTimestamp": "2018-04-07T12:13:25.156Z",
         |    "identityData": ${IdentityDataTestData.correctJson},
         |    "userAuthToken": "Bearer AbCdEf123456...",
         |    "headerData": { "...":"..." },
         |    "searchKeys": {
         |      "vrn": "123456789",
         |      "periodKey": "18AA"
         |    },
         |    "receiptData": {
         |      "language": "en",
         |      "checkYourAnswersSections": [
         |        {
         |          "title": "VAT details",
         |          "data": [
         |            {"questionId":"fooVatDetails1",
         |             "question": "VAT taxable sales ...",
         |             "answer": "Yes"},
         |            {"questionId":"fooVatDetails2",
         |             "question": "VAT start date",
         |             "answer": "The date the company is registered"},
         |            {"questionId":"fooVatDetails3",
         |             "question": "Other trading name",
         |             "answer": "Its a Mighty Fine Company"}
         |          ]
         |        },
         |        {
         |          "title": "Director details",
         |          "data": [
         |            {"questionId":"fooDirectorDetails1",
         |             "question": "Person registering the company for VAT",
         |             "answer": "Bob Bimbly Bobblous Bobbings"},
         |            {"questionId":"fooDirectorDetails2",
         |             "question": "Former name",
         |             "answer": "Dan Swales"},
         |            {"questionId":"fooDirectorDetails3",
         |             "question": "Date of birth",
         |             "answer": "1 January 2000"}
         |          ]
         |        },
         |        {
         |          "title": "Director addresses",
         |          "data": [
         |            {"questionId":"fooDirectorAddress1",
         |             "question": "Home address",
         |             "answers": [
         |              "98 Limbrick Lane",
         |              "Goring-by-sea",
         |              "Worthing",
         |              "BN12 6AG"
         |            ]},
         |            {"questionId":"fooDirectorAddress2",
         |             "question": "Lived at current address for more than 3 years",
         |             "answer": "Yes"}
         |          ]
         |        }
         |      ],
         |      "declaration": {
         |        "declarationText": "I confirm the data ....",
         |        "declarationName": "John Smith",
         |        "declarationRole": "Finance Director",
         |        "declarationConsent": true
         |      }
         |    }
         |  }
      """.stripMargin)

    val correctModel: Metadata = Metadata(
      "vat", "vat-registration", TextHtml, Some("426a1c28<snip>d6d363"), None,
      LocalDateTime.ofInstant(Instant.parse("2018-04-07T12:13:25.156Z"), ZoneId.of("UTC")), IdentityDataTestData.correctModel,
      "Bearer AbCdEf123456...", Map("..." -> "..."), SearchKeys("123456789", "18AA"),
      Some(ReceiptData(
        EN, Seq(
          Answers(
            "VAT details",
            Seq(
              Answer("fooVatDetails1", "VAT taxable sales ...", Some("Yes")),
              Answer("fooVatDetails2", "VAT start date", Some("The date the company is registered")),
              Answer("fooVatDetails3", "Other trading name", Some("Its a Mighty Fine Company"))
            )
          ),
          Answers(
            "Director details",
            Seq(
              Answer("fooDirectorDetails1", "Person registering the company for VAT", Some("Bob Bimbly Bobblous Bobbings")),
              Answer("fooDirectorDetails2", "Former name", Some("Dan Swales")),
              Answer("fooDirectorDetails3", "Date of birth", Some("1 January 2000"))
            )
          ),
          Answers(
            "Director addresses",
            Seq(
              Answer("fooDirectorAddress1", "Home address", None, Some(Seq(
                "98 Limbrick Lane",
                "Goring-by-sea",
                "Worthing",
                "BN12 6AG"
              ))),
              Answer("fooDirectorAddress2", "Lived at current address for more than 3 years", Some("Yes"))
            )
          )
        ),
        Declaration(
          "I confirm the data ....",
          "John Smith",
          Some("Finance Director"),
          declarationConsent = true
        )
      ))
    )
  }

  object FullRequestTestData {
    val correctJson: JsObject = Json.obj(
      "payload" -> "XXX-base64-CheckYourAnswersHTML-XXX",
      "metadata" -> MetadataTestData.correctJson
    )

    val correctModel: NrsReceiptRequestModel = NrsReceiptRequestModel(
      "XXX-base64-CheckYourAnswersHTML-XXX", MetadataTestData.correctModel
    )
  }

}
