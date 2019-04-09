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

package controllers.actions

import auth.MicroserviceAuthorisedFunctions
import javax.inject.Inject
import models.Error
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Request, Result}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.{ExecutionContext, Future}

class AuthorisedSubmitVatReturn @Inject()(val authConnector: AuthConnector) extends BaseController with AuthorisedFunctions {

  def async(vrn: String)(block: Request[_] => Future[Result])(implicit ec: ExecutionContext): Action[AnyContent] = Action.async {
    implicit request =>

      val enrolment: Enrolment = Enrolment("HMRC-MTD-VAT")
        .withIdentifier("VRN", vrn)
        .withDelegatedAuthRule("mtd-vat-auth")

      authorised(enrolment).retrieve(Retrievals.allEnrolments)(_ => block(request)) recover {
        case _: NoActiveSession =>
          Logger.debug(s"[AuthorisedSubmitVatReturn][async] - User has no active session")
          Unauthorized(Json.toJson(Error(UNAUTHORIZED.toString, "User has no active session")))
        case ex: AuthorisationException =>
          Logger.debug(s"[AuthorisedSubmitVatReturn][async] - Forbidden access to vat-returns service. Reason: ${ex.reason}")
          Forbidden(Json.toJson(Error(FORBIDDEN.toString, s"Forbidden access to vat-returns service. Reason: ${ex.reason}")))
      }
  }
}
