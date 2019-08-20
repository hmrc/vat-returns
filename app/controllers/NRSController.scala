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
import play.api.mvc.{Action, AnyContent, Result}
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
                Logger.debug(s"[NRSController][submitNRS] - request body to NRS contains incorrect JSON. Request body: ${request.body}")
                Logger.warn("[NRSController][submitNRS] - request body to NRS contains incorrect JSON")
                handleError(error)
            }
          case JsError(error) =>
            Logger.debug(s"[NRSController][submitNRS] - request body from submit-vat-return-frontend does not pass validation: $error")
            Logger.warn(s"[NRSController][submitNRS] - request body from submit-vat-return-frontend does not pass validation")
            Future.successful(BadRequest("Request body from submit-vat-return-frontend does not pass validation"))
        }
      case None =>
        Logger.debug(s"[NRSController][submitNRS] - request body cannot be parsed to JSON. Body: ${request.body}")
        Logger.warn("[NRSController][submitNRS] - request body cannot be parsed to JSON")
        Future.successful(BadRequest("Request body from submit-vat-return-frontend cannot be parsed to JSON."))
    }
  }

  private def handleError(error: Error): Result = {
    val CHECKSUM_FAILED = 419

    try {
      error.code.toInt match {
        case BAD_REQUEST => BadRequest(error.reason)
        case UNAUTHORIZED => Unauthorized(error.reason)
        case CHECKSUM_FAILED => BadRequest(error.reason)
        case unknown500ErrorOr404 if unknown500ErrorOr404 == NOT_FOUND || (unknown500ErrorOr404 >= 500 && unknown500ErrorOr404 < 600) =>
          Status(unknown500ErrorOr404)(error.reason)
        case _ => BadRequest(error.toJson)
      }
    } catch {
      case _: Throwable => BadRequest(error.toJson)
    }
  }
}

