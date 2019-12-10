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
import models.Error._
import models._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.VatReturnsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitVatReturnController @Inject()(vatReturnsService: VatReturnsService,
                                          authorisedAction: AuthorisedSubmitVatReturn,
                                          cc: ControllerComponents)
                                         (implicit ec: ExecutionContext) extends BackendController(cc) {

  def submitVatReturn(vrn: String): Action[AnyContent] = authorisedAction.async(vrn) { implicit request =>
    val requestAsJson: Option[VatReturnDetail] = request.body.asJson match {
      case Some(validJson) => validJson.asOpt[VatReturnDetail]
      case None =>
        Logger.warn("[SubmitVatReturnController][submitVatReturn] Issue parsing body as json")
        Logger.debug("[SubmitVatReturnController][submitVatReturn] The body provided in the request is not valid Json")
        None
    }

    requestAsJson match {
      case Some(vatReturnModel) =>
        request.headers.get("OriginatorID") match {
          case Some(id) if (id == VATUI.id) || (id == MDTP.id) =>
            submission(vrn, vatReturnModel, id)
          case Some(invalid) =>
            Logger.warn(s"[SubmitVatReturnsController][SubmitVatReturn] Invalid OriginatorID header value: $invalid")
            Future.successful(BadRequest(Error("400", "Invalid OriginatorID header value").toJson))
          case None =>
            Logger.warn(s"[SubmitVatReturnsController][SubmitVatReturn] No OriginatorID found in header")
            Future.successful(BadRequest(Error("400", "No OriginatorID found in header").toJson))
        }
      case None =>
        Logger.debug("[SubmitVatReturnsController][SubmitVatReturn] An error occurred while trying to parse incoming Json")
        Future.successful(InternalServerError(Json.toJson(InvalidJsonResponse.toJson)))
    }
  }

  private def submission(vrn: String, vatReturnModel: VatReturnDetail, originatorID: String)
                        (implicit hc: HeaderCarrier)= {
    vatReturnsService.submitVatReturn(vrn, vatReturnModel, originatorID).map {
      case Right(responseModel) =>
        Ok(Json.toJson(responseModel))
      case Left(error) =>
        Logger.warn("[SubmitVatReturnsController][submission] Error occurred while trying to submit return")
        Logger.debug(
          "[SubmitVatReturnsController][submission] The following error occurred while trying to submit a return:"
            + error.error.toJson
        )
        Status(error.status)(Json.toJson(error.toJson))
    }
  }
}
