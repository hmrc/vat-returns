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


  object Answer {

    object MockJson {
      val correctJsonSingleLineAnswer: JsObject = Json.obj(
        "questionId" -> "00192IIO",
        "question" -> "WHERE ARE THEY!?!?!",
        "answer" -> "WAHOO WAHAHAHAHA You have nothing, nothing to threaten me with"
      )

      val correctJsonMultiLineAnswer: JsObject = Json.obj(
        "questionId" -> "00192IID",
        "question" -> "What's your name, traveller?",
        "answers" -> Json.arr(
          "Hang on, let me choose my race",
          "And then my gender",
          "I suppose I'll change my entire facial structure",
          "Oh, and my name is..."
        )
      )
    }

    object Models {
      val correctModelSingleLineAnswer: NrsAnswer = NrsAnswer(
        "00192IIO",
        "WHERE ARE THEY!?!?!",
        Some("WAHOO WAHAHAHAHA You have nothing, nothing to threaten me with")
      )

      val correctModelMultiLineAnswer: NrsAnswer = NrsAnswer(
        "00192IID",
        "What's your name, traveller?",
        None,
        Some(Seq(
          "Hang on, let me choose my race",
          "And then my gender",
          "I suppose I'll change my entire facial structure",
          "Oh, and my name is..."
        ))
      )
    }

  }

  object Answers {
    val correctJson: JsObject = Json.obj(
      "title" -> "Things Batman says and does",
      "data" -> Json.arr(
        Answer.MockJson.correctJsonSingleLineAnswer,
        Json.obj(
          "questionId" -> "00192IIP",
          "question" -> "Who are y-",
          "answer" -> "I'M BATAMAAAAAAAAAAAN!!!!!!!"
        )
      )
    )

    val correctModel: NrsAnswers = NrsAnswers(
      "Things Batman says and does",
      Seq(
        Answer.Models.correctModelSingleLineAnswer,
        NrsAnswer(
          "00192IIP",
          "Who are y-",
          Some("I'M BATAMAAAAAAAAAAAN!!!!!!!")
        )
      )
    )
  }

  object Declaration {
    val correctJson: JsValue = Json.parse(
      """
        |{
        | "declarationText": "I confirm the data...",
        | "declarationName": "Scarlett Flamberg",
        | "declarationRole": "Warrior of Light",
        | "declarationConsent": true
        |}
      """.stripMargin)

    val correctModel: NrsDeclaration = NrsDeclaration(
      "I confirm the data...",
      "Scarlett Flamberg",
      "Warrior of Light",
      declarationConsent = true
    )
  }

  object Receipt {
    val correctJson: JsObject = Json.obj(
      "language" -> "en",
      "checkYourAnswersSections" -> Json.arr(
        Answers.correctJson
      ),
      "declaration" -> Declaration.correctJson
    )

    val correctModel = NrsReceiptData(
      EN, Seq(Answers.correctModel), Declaration.correctModel
    )
  }

  object IdentityData {


    val correctJson: JsValue = Json.parse("""{
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

    val correctModel: NrsIdentityData = NrsIdentityData(
      "some-id", "some-id", "TZRXXV",
      IdentityCredentials("12345-credId", "GovernmentGateway"),
      200, "DH00475D", "Utr",
      IdentityName("test", "test"),
      LocalDate.parse("1985-01-01"), "test@test.com",
      IdentityAgentInformation("TZRXXV", "Bodgitt & Legget LLP", "BDGL"), "GroupId", "admin",
      IdentityMdtpInformation("DeviceId", "SessionId"),
      IdentityItmpName("test", "test", "test"), LocalDate.parse("1985-01-01"),
      IdentityItmpAddress("Line 1", "NW94HD", "United Kingdom", "UK"), "Agent", "strong",
      IdentityLoginTimes(
        LocalDateTime.ofInstant(Instant.parse("2016-11-27T09:00:00.000Z"), ZoneId.of("UTC")),
        LocalDateTime.ofInstant(Instant.parse("2016-11-01T12:00:00.000Z"), ZoneId.of("UTC"))
      )
    )
  }

  object Metadata {
    val correctJson: JsValue = Json.parse(
      s"""
        |{
        |    "businessId": "vat",
        |    "notableEvent": "vat-registration",
        |    "payloadContentType": "text/html",
        |    "payloadSha256Checksum": "426a1c28<snip>d6d363",
        |    "userSubmissionTimestamp": "2018-04-07T12:13:25.156Z",
        |    "identityData": ${IdentityData.correctJson},
        |    "userAuthToken": "Bearer AbCdEf123456...",
        |    "headerData": { "...":"..." },
        |    "searchKeys": { "...":"..." },
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

    val correctModel: NrsMetadata = NrsMetadata(
      "vat", "vat-registration", TextHtml, Some("426a1c28<snip>d6d363"), None,
      LocalDateTime.ofInstant(Instant.parse("2018-04-07T12:13:25.156Z"), ZoneId.of("UTC")), IdentityData.correctModel,
      "Bearer AbCdEf123456...", Map("..." -> "..."), Map("..." -> "..."),
      NrsReceiptData(
        EN, Seq(
          NrsAnswers(
            "VAT details",
            Seq(
              NrsAnswer("fooVatDetails1", "VAT taxable sales ...", Some("Yes")),
              NrsAnswer("fooVatDetails2", "VAT start date", Some("The date the company is registered")),
              NrsAnswer("fooVatDetails3", "Other trading name", Some("Its a Mighty Fine Company"))
            )
          ),
          NrsAnswers(
            "Director details",
            Seq(
              NrsAnswer("fooDirectorDetails1", "Person registering the company for VAT", Some("Bob Bimbly Bobblous Bobbings")),
              NrsAnswer("fooDirectorDetails2", "Former name", Some("Dan Swales")),
              NrsAnswer("fooDirectorDetails3", "Date of birth", Some("1 January 2000"))
            )
          ),
          NrsAnswers(
            "Director addresses",
            Seq(
              NrsAnswer("fooDirectorAddress1", "Home address", None, Some(Seq(
                "98 Limbrick Lane",
                "Goring-by-sea",
                "Worthing",
                "BN12 6AG"
              ))),
              NrsAnswer("fooDirectorAddress2", "Lived at current address for more than 3 years", Some("Yes"))
            )
          )
        ),
        NrsDeclaration(
          "I confirm the data ....",
          "John Smith",
          "Finance Director",
          declarationConsent = true
        )
      )
    )
  }

  object FullRequest {
    val correctJson: JsObject = Json.obj(
      "payload" -> "XXX-base64-CheckYourAnswersHTML-XXX",
      "metadata" -> Metadata.correctJson
    )

    val correctModel: NrsRequestModel = NrsRequestModel(
      "XXX-base64-CheckYourAnswersHTML-XXX", Metadata.correctModel
    )
  }
}
