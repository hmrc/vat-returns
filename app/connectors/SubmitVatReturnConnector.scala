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

package connectors

import config.MicroserviceAppConfig
import connectors.httpParsers.SubmitVatReturnHttpParser._
import models.{Error, ErrorResponse, SuccessModel, VatReturnSubmission}
import play.api.http.Status.BAD_GATEWAY
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpException, HttpReads}
import utils.LoggerUtil

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmitVatReturnConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) extends LoggerUtil {

  private lazy val desVatReturnsUrl: String => String = vrn => appConfig.desServiceUrl + appConfig.desSubmitVatReturnPath + vrn

  def submitVatReturn(vrn: String, model: VatReturnSubmission, originatorID: String)
                     (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[SuccessModel]] = {

    val desHeaders = Seq("Authorization" -> s"Bearer ${appConfig.desToken}", "Content-Type" -> "application/json",
      "Environment" -> appConfig.desEnvironment, "OriginatorID" -> originatorID)

    val hc = headerCarrier.copy(authorization = None)

    logger.debug(s"[SubmitVatReturnConnector][submitVatReturn] Submitting VAT Return to URL: ${desVatReturnsUrl(vrn)}." +
      s" Body: ${Json.toJson(model)}")
    logger.debug(s"[SubmitVatReturnConnector][submitVatReturn] Headers: $desHeaders")
    http.POST[VatReturnSubmission, HttpGetResult[SuccessModel]](desVatReturnsUrl(vrn), model, desHeaders)(
      implicitly[Writes[VatReturnSubmission]],
      implicitly[HttpReads[HttpGetResult[SuccessModel]]],
      hc,
      implicitly[ExecutionContext]
    ).recover {
      case ex: HttpException =>
        logger.warn(s"[NrsConnector][nrsReceiptSubmission] - HTTP exception received: ${ex.message}")
        Left(ErrorResponse(BAD_GATEWAY, Error("BAD_GATEWAY", ex.message)))
    }
  }
}
