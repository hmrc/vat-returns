/*
 * Copyright 2018 HM Revenue & Customs
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
import models.{VatReturn, VatReturnFilters}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatReturnsConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) {

  private[connectors] def setupDesVatReturnsUrl(vrn: String): String = appConfig.desServiceUrl +
    appConfig.setupDesReturnsStartPath + vrn

  def getVatReturns(vrn: String, queryParameters: VatReturnFilters)
                   (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[VatReturn]] = {

    val url = setupDesVatReturnsUrl(vrn)
    val desHC = headerCarrier.copy(authorization = Some(Authorization(s"Bearer ${appConfig.desToken}")))
      .withExtraHeaders("Environment" -> appConfig.desEnvironment)

    http.GET(url, queryParameters.toSeqQueryParams)(VatReturnReads, desHC, ec).map {
      case vatReturns@Right(_) => vatReturns
      case error@Left(_) => error
    }
  }
}
