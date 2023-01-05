/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.VatReturnsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatReturnsController @Inject()(val authenticate: AuthAction,
                                     val vatReturnsService: VatReturnsService,
                                     cc: ControllerComponents)(
                                     implicit val ec: ExecutionContext)
  extends BackendController(cc) with LoggerUtil {

  def getVatReturns(vrn: String, filters: VatReturnFilters): Action[AnyContent] =
    authenticate.async {
      implicit authorisedUser =>
        if (isInvalidVrn(vrn)) {
          logger.warn(s"[VatReturnsController][getVatReturns] Invalid VRN '$vrn' received in request.")
          Future.successful(BadRequest(Json.toJson(InvalidVrn)))
        } else {
          retrieveVatReturns(vrn, filters)
        }
    }

  private def retrieveVatReturns(vrn: String, filters: VatReturnFilters)(implicit hc: HeaderCarrier) = {
    logger.debug(s"[VatReturnsController][retrieveVatReturns] Calling VatReturnsService.getVatReturns")

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
