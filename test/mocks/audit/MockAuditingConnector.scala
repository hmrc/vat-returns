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

package mocks.audit

import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.model.{DataEvent, ExtendedDataEvent}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}

trait MockAuditingConnector extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuditConnector)
  }

  val mockAuditConnector: AuditConnector = mock[AuditConnector]

  def mockSendAuditEvent(data: DataEvent)(result: AuditResult): OngoingStubbing[Future[AuditResult]] = {
    when(mockAuditConnector.sendEvent(ArgumentMatchers.refEq(data, "eventId", "generatedAt"))(
      ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext]
    )) thenReturn Future.successful(result)
  }

  def verifySendAuditEvent(data: DataEvent): Unit =
    verify(mockAuditConnector).sendEvent(ArgumentMatchers.refEq(data, "eventId", "generatedAt"))(
      ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext]
    )

  def mockSendAuditEvent(data: ExtendedDataEvent)(result: AuditResult): OngoingStubbing[Future[AuditResult]] = {
    when(mockAuditConnector.sendExtendedEvent(ArgumentMatchers.refEq(data, "eventId", "generatedAt"))(
      ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext]
    )) thenReturn Future.successful(result)
  }

  def verifySendAuditEvent(data: ExtendedDataEvent): Unit =
    verify(mockAuditConnector).sendExtendedEvent(ArgumentMatchers.refEq(data, "eventId", "generatedAt"))(
      ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext]
    )
}
