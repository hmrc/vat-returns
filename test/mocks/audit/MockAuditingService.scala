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

package mocks.audit

import audit.AuditingService
import audit.models.{AuditModel, ExtendedAuditModel}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Success
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}

trait MockAuditingService extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuditingService)
  }

  val mockAuditingService: AuditingService = mock[AuditingService]

  def setupMockAuditEventResponse(data: ExtendedAuditModel): OngoingStubbing[Future[AuditResult]] =
    when(mockAuditingService.audit(
      ArgumentMatchers.eq(data)
    )(
      ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext]
    )).thenReturn(Future.successful(Success))

  def setupMockAuditEventResponse(data: AuditModel): OngoingStubbing[Future[AuditResult]] =
    when(mockAuditingService.audit(
      ArgumentMatchers.eq(data)
    )(
      ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext]
    )).thenReturn(Future.successful(Success))

  def verifyAuditEvent(data: AuditModel): Unit =
    verify(mockAuditingService).audit(
      ArgumentMatchers.eq(data)
    )(
      ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext]
    )

  def verifyAuditEvent(data: ExtendedAuditModel): Unit =
    verify(mockAuditingService).audit(
      ArgumentMatchers.eq(data)
    )(
      ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext]
    )
}
