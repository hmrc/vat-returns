/*
 * Copyright 2024 HM Revenue & Customs
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

package services

import java.time.{LocalDateTime, ZoneOffset}
import audit.AuditingService
import audit.models.{VatReturnRequestAuditModel, VatReturnResponseAuditModel}

import javax.inject.{Inject, Singleton}
import connectors.{SubmitVatReturnConnector, VatReturnsConnector}
import models._
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatReturnsService @Inject()(val VatReturnsConnector: VatReturnsConnector,
                                  submitVatReturnConnector: SubmitVatReturnConnector,
                                  val auditingService: AuditingService) extends LoggerUtil {

  def getVatReturns(vrn: String, queryParameters: VatReturnFilters)
                       (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, VatReturnDetail]] = {

    logger.debug(s"[VatReturnsService][getVatReturns] Auditing Vat Returns request")
    auditingService.audit(VatReturnRequestAuditModel(vrn, queryParameters))

    logger.debug(s"[VatReturnsService][getVatReturns] Calling vatReturnsConnector with Vrn: $vrn\nParams: $queryParameters")
    VatReturnsConnector.getVatReturns(vrn, queryParameters).map {
      case success@Right(vatReturns) =>
        logger.debug(s"[VatReturnsService][getVatReturns] Auditing Vat Returns response")
        auditingService.audit(VatReturnResponseAuditModel(vrn, vatReturns))
        success
      case error@Left(_) =>
        error
    }
  }

  def submitVatReturn(vrn: String, model: VatReturnDetail, originatorID: String)
                     (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, SuccessModel]] = {
    val submissionModel = VatReturnSubmission(
      model.periodKey,
      model.vatDueSales,
      model.vatDueAcquisitions,
      model.vatDueTotal,
      model.vatReclaimedCurrPeriod,
      model.vatDueNet,
      model.totalValueSalesExVAT,
      model.totalValuePurchasesExVAT,
      model.totalValueGoodsSuppliedExVAT,
      model.totalAllAcquisitionsExVAT,
      model.agentReferenceNumber,
      LocalDateTime.now(ZoneOffset.UTC)
    )
    logger.debug(s"[VatReturnsService][submitVatReturn] Calling SubmitVatReturnConnector with model: $submissionModel")
    submitVatReturnConnector.submitVatReturn(vrn, submissionModel, originatorID)
  }
}

