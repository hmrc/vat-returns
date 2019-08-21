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
import models.Error
import models.nrs.NrsReceiptRequestModel
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, AnyContent}
import services.NrsSubmissionService
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NRSController @Inject()(authorisedAction: AuthorisedSubmitVatReturn,
                              nrsSubmissionService: NrsSubmissionService)
                             (implicit ec: ExecutionContext) extends BaseController {

  def submitNRS(vrn: String): Action[AnyContent] = authorisedAction.async(vrn) { implicit request =>

    request.body.asJson match {
      case Some(json) =>
        json.validate[NrsReceiptRequestModel] match {
          case JsSuccess(model, _) =>
            nrsSubmissionService.nrsReceiptSubmission(model) map {
              case Right(successModel) =>
                Logger.debug("[NRSController][submitNRS] - successful post to NRS")
                Accepted(Json.toJson(successModel))
              case Left(error) =>
                Logger.debug(s"[NRSController][submitNRS] - NRS submission failed. Response body: ${error.reason}")
                Logger.warn("[NRSController][submitNRS] - NRS submission failed.")
                Status(error.code.toInt)(Json.toJson(error.reason))
            }
          case JsError(error) =>
            Logger.debug(s"[NRSController][submitNRS] - request body does not pass validation: $error")
            Logger.warn(s"[NRSController][submitNRS] - request body does not pass validation")
            Future.successful(BadRequest(Json.toJson(Error("400", "Request body does not pass validation"))))
        }
      case None =>
        Logger.debug(s"[NRSController][submitNRS] - request body cannot be parsed to JSON. Body: ${request.body}")
        Logger.warn("[NRSController][submitNRS] - request body cannot be parsed to JSON")
        Future.successful(BadRequest(Json.toJson(Error("400", "Request body cannot be parsed to JSON."))))
    }
  }
}

