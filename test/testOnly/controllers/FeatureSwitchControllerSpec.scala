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

package testOnly.controllers

import base.SpecBase
import config.featureSwitch.FeatureSwitchModel
import org.scalatest.BeforeAndAfterEach
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import testonly.controllers.FeatureSwitchController

import scala.concurrent.Future

class FeatureSwitchControllerSpec extends SpecBase with BeforeAndAfterEach {

  private lazy val target = new FeatureSwitchController(mockAppConfig, controllerComponents)

  override def beforeEach() {
    mockAppConfig.features.useStubFeature(true)
  }

  "Calling the .get action" should {

    lazy val result: Future[Result] = target.get()(fakeRequest)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return JSON" in {
      contentType(result) shouldBe Some("application/json")
    }

    "return feature switch configuration" in {
      await(jsonBodyOf(result)) shouldEqual Json.toJson(
        FeatureSwitchModel(useStubFeature = true))
    }
  }

  "Calling the .update action" should {

    val body = Json.toJson(FeatureSwitchModel(
      useStubFeature = false)
    )

//    val result = target.update()(FakeRequest(POST, "").withHeaders((CONTENT_TYPE, "application/json")).withJsonBody(body)).run()

    val result: Future[Result] = call(target.update(), FakeRequest(POST, "")
      .withHeaders((CONTENT_TYPE, "application/json"))
      .withJsonBody(body))

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return new feature switch configuration" in {
      await(jsonBodyOf(result)) shouldEqual body
    }
  }
}
