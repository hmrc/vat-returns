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

package services

import connectors.NrsConnector
import connectors.httpParsers.NrsResponseParsers.SubmissionResult
import javax.inject.Inject
import models.nrs.NrsReceiptRequestModel
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class NrsSubmissionService @Inject()(connector: NrsConnector) {
  def nrsReceiptSubmission(data: NrsReceiptRequestModel)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[SubmissionResult] = {
    connector.nrsReceiptSubmission(data)
  }
}
