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

package connectors

import config.MicroserviceAppConfig
import connectors.httpParsers.NrsResponseParsers._
import javax.inject.Inject
import models.Error
import models.nrs.{AppJson, NrsReceiptRequestModel}
import play.api.Logger
import uk.gov.hmrc.http.{GatewayTimeoutException, HeaderCarrier}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import play.api.http.Status.GATEWAY_TIMEOUT

import scala.concurrent.{ExecutionContext, Future}

class NrsConnector @Inject()(http: HttpClient, appConfig: MicroserviceAppConfig) {
  private def urlToUse(vrn: String): String = appConfig.nrsSubmissionEndpoint + (if (appConfig.features.useStubFeature()) s"/$vrn" else "")

  def nrsReceiptSubmission(data: NrsReceiptRequestModel)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[SubmissionResult] = {
    http.POST[NrsReceiptRequestModel, SubmissionResult](
      urlToUse(data.metadata.searchKeys.vrn),
      data,
      Seq(
        "Content-Type" -> AppJson,
        "X-API-Key" -> appConfig.nrsApiKey
      )
    )
  }.recover {
    case _: GatewayTimeoutException => Left(Error(GATEWAY_TIMEOUT.toString, "Request to NRS timed out."))
    case error: Throwable =>
      Logger.error("[NrsConnector][nrsReceiptSubmission] Unexpected exception returned from NRS", error)
      Left(Error("UNEXPECTED_EXCEPTION", error.getMessage))
  }
}
