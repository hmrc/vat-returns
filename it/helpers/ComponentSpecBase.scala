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

package helpers

import akka.stream.Materializer
import config.MicroserviceAppConfig
import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.{BodyWritable, WSResponse}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.http.HeaderCarrier
import org.scalatest.matchers.should.Matchers

trait ComponentSpecBase extends TestSuite with CustomMatchers
  with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience with Matchers
  with WireMockHelper with BeforeAndAfterEach with BeforeAndAfterAll with Eventually {

  val mockHost: String = WireMockHelper.wireMockHost
  val mockPort: String = WireMockHelper.wireMockPort.toString
  val mockUrl: String = s"http://$mockHost:$mockPort"
  val mockToken = "localToken"
  val mockEnvironment = "localEnvironment"
  val mockEndpointStart = "/vat/returns/vrn/"
  val mockSubmitVatReturn = "/enterprise/return/vat/"

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val mat: Materializer = app.injector.instanceOf[Materializer]
  lazy val mockAppConfig: MicroserviceAppConfig = app.injector.instanceOf[MicroserviceAppConfig]

  def config: Map[String, String] = Map(
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.des.url" -> mockUrl,
    "microservice.services.des.endpoints.vatReturnsUrlStart" -> mockEndpointStart,
    "microservice.services.des.endpoints.submitVatReturn" -> mockSubmitVatReturn,
    "microservice.services.des.environment" -> mockEnvironment,
    "microservice.services.des.authorization-token" -> mockToken,
    "microservice.services.nrs.receipts.host" -> s"http://$mockHost",
    "microservice.services.nrs.receipts.port" -> mockPort
  )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockAppConfig.features.useStubFeature(true)
  }

  def post[T](path: String, headers: Map[String, String] = Map.empty)(body: T)(implicit wrt: BodyWritable[T]): WSResponse =
    await(buildClient(path, headers).post(body))

  def get(uri: String): WSResponse = {
    await(buildClient(uri).withHttpHeaders("Authorization" -> "localToken").get())
  }
}
