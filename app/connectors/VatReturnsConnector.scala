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

package connectors

import config.MicroserviceAppConfig
import connectors.httpParsers.VatReturnsHttpParser._

import javax.inject.{Inject, Singleton}
import models.{VatReturnDetail, VatReturnFilters}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatReturnsConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) extends LoggerUtil {

  private[connectors] def setupDesVatReturnsUrl(vrn: String): String = appConfig.desServiceUrl +
    appConfig.setupDesReturnsStartPath + vrn

  val desHeaders = Seq("Authorization" -> s"Bearer ${appConfig.desToken}", "Environment" -> appConfig.desEnvironment)

  def getVatReturns(vrn: String, queryParameters: VatReturnFilters)
                   (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[VatReturnDetail]] = {

    val url = setupDesVatReturnsUrl(vrn)
    val hc = headerCarrier.copy(authorization = None)

    logger.debug(s"[VatReturnsConnector][getVatReturns] - Calling GET $url \nHeaders: $desHeaders\n QueryParams: $queryParameters")
    http.GET(url, queryParameters.toSeqQueryParams, desHeaders)(VatReturnReads, hc, ec).map {
      case vatReturns@Right(_) => vatReturns
      case error@Left(message) =>
        logger.warn("[VatReturnsConnector][getVatReturns] Error Received. Message: " + message)
        error
    }
  }
}
