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

package controllers

import controllers.actions.AuthorisedSubmitVatReturn
import javax.inject.{Inject, Singleton}
import models.nrs.RequestModel
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NRSController @Inject()(authorisedAction: AuthorisedSubmitVatReturn,
                              nrsSubmissionService: NrsSubmissionService)
                             (implicit ec: ExecutionContext) extends BaseController {

  def submitNRS(vrn: String): Action[AnyContent] = authorisedAction.async(vrn) { implicit request =>
    val requestAsJson: Option[RequestModel] = request.body.asJson match {
      case Some(validJson) => validJson.asOpt[RequestModel]
      case None => None
    }

    requestAsJson match {
      case Some(model) => nrsSubmissionService.nrsReceiptSubmission(model) map {
        case Right(successModel) => Ok(Json.toJson(successModel))
        case Left(error) => InternalServerError(error)
      }
    }


  }

}

trait NrsReceiptSuccessModel {
  val nrsSubmissionId: String
}

trait NrsSubmissionService {
  def nrsReceiptSubmission(data: RequestModel)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Either[Error, NrsReceiptSuccessModel]]
}
