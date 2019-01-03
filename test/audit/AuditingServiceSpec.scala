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

package audit

import audit.models.{AuditModel, ExtendedAuditModel}
import base.SpecBase
import mocks.audit.MockAuditingConnector
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.AuditResult.{Disabled, Failure, Success}
import uk.gov.hmrc.play.audit.model.{DataEvent, ExtendedDataEvent}

class AuditingServiceSpec extends SpecBase with MockAuditingConnector {

  object TestAuditingService extends AuditingService(mockAppConfig, mockAuditConnector)

  "For a DataEvent model" when {

    object TestAuditModel extends AuditModel {
      override val auditType: String = "testEvent"
      override val transactionName: String = "testTransaction"
      override val detail: Seq[(String, String)] = Seq(
        "testDetailA" -> "foo",
        "testDetailB" -> "bar"
      )
    }

    lazy val testAuditData: DataEvent =
      TestAuditingService.toDataEvent(mockAppConfig.appName, TestAuditModel,"/dummy/referrer/path")

    "AuditingService.toDataEvent(x: DataEvent) method" should {

      "correctly format the data to a Audit DataEvent Model" which {

        "has the correct auditSource" in {
          testAuditData.auditSource shouldBe mockAppConfig.appName
        }

        "has the correct auditType" in {
          testAuditData.auditType shouldBe TestAuditModel.auditType
        }

        "has the correct audit tags" in {
          testAuditData.tags shouldBe AuditExtensions.auditHeaderCarrier(hc).toAuditTags(TestAuditModel.transactionName,
            "/dummy/referrer/path")
        }

        "has the correct audit detail" in {
          testAuditData.detail shouldBe AuditExtensions.auditHeaderCarrier(hc).toAuditDetails(TestAuditModel.detail: _*)
        }
      }
    }

    "AuditingService.audit(x: DataEvent)" when {

      "provided with an AuditModel" should {

        "and a success response is mocked should extract the data and pass it into the AuditConnector" in {
          mockSendAuditEvent(testAuditData)(Success)
          await(TestAuditingService.audit(TestAuditModel)) shouldBe Success
          verifySendAuditEvent(testAuditData)
        }

        "and a failure response is mocked should extract the data and pass it into the AuditConnector" in {
          mockSendAuditEvent(testAuditData)(Failure("Oh no, an error!"))
          await(TestAuditingService.audit(TestAuditModel)) shouldBe Failure("Oh no, an error!")
          verifySendAuditEvent(testAuditData)
        }

        "and auditing is disabled response is mocked should extract the data and pass it into the AuditConnector" in {
          mockSendAuditEvent(testAuditData)(Disabled)
          await(TestAuditingService.audit(TestAuditModel)) shouldBe Disabled
          verifySendAuditEvent(testAuditData)
        }
      }
    }
  }

  "For an ExtendedDataEvent model" when {

    object TestExtendedAuditModel extends ExtendedAuditModel {
      override val auditType: String = "testEvent"
      override val transactionName: String = "testTransaction"
      override val detail: JsValue = Json.obj(
        "testDetailA" -> "foo",
        "testDetailB" -> "bar",
        "testNestedA" -> Json.arr(
          Json.obj(
            "nestedA" -> "nestedFoo",
            "nestedB" -> "nestedBar"
          ),
          Json.obj(
            "nestedA" -> "nestedFoo2",
            "nestedB" -> "nestedBar2"
          )
        )
      )
    }

    lazy val testAuditData: ExtendedDataEvent = TestAuditingService.toDataEvent(mockAppConfig.appName,
      TestExtendedAuditModel, "/dummy/referrer/path")

    "AuditingService.toDataEvent(x: ExtendedDataEvent) method" should {

      "correctly format the data to a Audit DataEvent Model" which {

        "has the correct auditSource" in {
          testAuditData.auditSource shouldBe mockAppConfig.appName
        }

        "has the correct auditType" in {
          testAuditData.auditType shouldBe TestExtendedAuditModel.auditType
        }

        "has the correct audit tags" in {
          testAuditData.tags shouldBe AuditExtensions.auditHeaderCarrier(hc).toAuditTags(TestExtendedAuditModel.transactionName,
            "/dummy/referrer/path")
        }

        "has the correct audit detail" in {
          val expected: JsValue = Json.obj(
            "ipAddress"->"-",
            "Authorization"->"-",
            "token"->"-",
            "deviceID"->"-",
            "testDetailA"->"foo",
            "testDetailB"->"bar",
            "testNestedA"-> Json.arr(
              Json.obj(
                "nestedA"->"nestedFoo",
                "nestedB"->"nestedBar"
              ),
              Json.obj(
                "nestedA"->"nestedFoo2",
                "nestedB"->"nestedBar2"
              )
            )
          )

          testAuditData.detail shouldBe expected
        }
      }
    }

    "AuditingService.audit(x: ExtendedDataEvent)" when {

      "provided with an AuditModel" should {

        "and a success response is mocked should extract the data and pass it into the AuditConnector" in {
          mockSendAuditEvent(testAuditData)(Success)
          await(TestAuditingService.audit(TestExtendedAuditModel)) shouldBe Success
          verifySendAuditEvent(testAuditData)
        }

        "and a failure response is mocked should extract the data and pass it into the AuditConnector" in {
          mockSendAuditEvent(testAuditData)(Failure("Oh no, an error!"))
          await(TestAuditingService.audit(TestExtendedAuditModel)) shouldBe Failure("Oh no, an error!")
          verifySendAuditEvent(testAuditData)
        }

        "and auditing is disabled response is mocked should extract the data and pass it into the AuditConnector" in {
          mockSendAuditEvent(testAuditData)(Disabled)
          await(TestAuditingService.audit(TestExtendedAuditModel)) shouldBe Disabled
          verifySendAuditEvent(testAuditData)
        }
      }
    }
  }
}
