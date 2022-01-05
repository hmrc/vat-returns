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

package base

import config.MicroserviceAppConfig
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice._
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import uk.gov.hmrc.http.HeaderCarrier
import utils.MaterializerSupport
import play.api.http.HeaderNames.REFERER

import java.time.LocalDate
import scala.concurrent.ExecutionContext

trait SpecBase extends AnyWordSpecLike with Matchers with OptionValues with GuiceOneAppPerSuite with MaterializerSupport with Injecting {

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")
  def emptyFakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val mockAppConfig: MicroserviceAppConfig = inject[MicroserviceAppConfig]

  implicit lazy val controllerComponents = inject[ControllerComponents]

  implicit lazy val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(REFERER -> "/dummy/referrer/path")
  implicit lazy val ec: ExecutionContext = inject[ExecutionContext]

  def stringToDate(date: String): LocalDate = {LocalDate.parse(date)}

}
