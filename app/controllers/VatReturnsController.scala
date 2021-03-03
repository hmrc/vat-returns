/*
 * Copyright 2021 HM Revenue & Customs
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

import controllers.actions.AuthAction
import javax.inject.{Inject, Singleton}
import models._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.VatReturnsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class VatReturnsController @Inject()(val authenticate: AuthAction, val vatReturnsService: VatReturnsService, cc: ControllerComponents) extends BackendController(cc) {

  def getVatReturns(vrn: String, filters: VatReturnFilters): Action[AnyContent] =
    authenticate.async {
      implicit authorisedUser =>
        if (isInvalidVrn(vrn)) {
          Logger.warn(s"[VatReturnsController][getVatReturns] Invalid VRN '$vrn' received in request.")
          Future.successful(BadRequest(Json.toJson(InvalidVrn)))
        } else {
          retrieveVatReturns(vrn, filters)
        }
    }

  private def retrieveVatReturns(vrn: String, filters: VatReturnFilters)(implicit hc: HeaderCarrier) = {
    Logger.debug(s"[VatReturnsController][retrieveVatReturns] Calling VatReturnsService.getVatReturns")

    vatReturnsService.getVatReturns(vrn, filters).map {
      case _@Right(vatReturns) => Ok(Json.toJson(vatReturns))
      case _@Left(error) => error.error match {
        case singleError: Error => Status(error.status)(Json.toJson(singleError))
        case multiError: MultiError => Status(error.status)(Json.toJson(multiError))
      }
    }
  }

  private def isInvalidVrn(vrn: String): Boolean = {
    val vrnRegex = """^\d{9}$"""
    !vrn.matches(vrnRegex)
  }
}
