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

package controllers.actions

import javax.inject.Inject
import models.Error
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request, Result}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.~
import auth.AuthEnrolmentKeys._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class AuthorisedSubmitVatReturn @Inject()(val authConnector: AuthConnector, cc: ControllerComponents)
  extends BackendController(cc) with AuthorisedFunctions with LoggerUtil {

  def async(vrn: String)(block: Request[AnyContent] => Future[Result])(implicit ec: ExecutionContext): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(Retrievals.affinityGroup and Retrievals.allEnrolments) {
        case Some(affinityGroup) ~ enrolments =>
          if(affinityGroup == Agent) {
            authoriseAsAgent(block, vrn)
          } else {
            authoriseAsIndividual(enrolments, block, vrn)
          }
        case _ => Future.successful(forbiddenAction("Failed to retrieve enrolments"))
      } recover authExceptionAction
  }

  private def authoriseAsIndividual(enrolments: Enrolments,
                                    block: Request[AnyContent] => Future[Result],
                                    requestedVrn: String)(implicit request: Request[AnyContent]): Future[Result] = {
    enrolments.enrolments.collectFirst {
      case Enrolment(`vatEnrolmentId`, Seq(EnrolmentIdentifier(_, vrn)), _, _) =>
        if(requestedVrn == vrn) {
          block(request)
        } else {
          Future.successful(forbiddenAction("Requested VRN does not match VRN in auth header"))
        }
    } getOrElse Future.successful(forbiddenAction(s"User does not have $vatEnrolmentId enrolment"))
  }

  private def authoriseAsAgent(block: Request[AnyContent] => Future[Result],
                               requestedVrn: String)(implicit request: Request[AnyContent], ec: ExecutionContext): Future[Result] = {

    val agentDelegatedAuthorityRule: String => Enrolment = vrn =>
      Enrolment(vatEnrolmentId)
        .withIdentifier(vatIdentifierId, vrn)
        .withDelegatedAuthRule(delegatedAuthRule)

    authorised(agentDelegatedAuthorityRule(requestedVrn))
      .retrieve(Retrievals.allEnrolments) {
        enrolments =>
          enrolments.enrolments.collectFirst {
            case Enrolment(`agentEnrolmentId`, Seq(_), _, _) => block(request)
          } getOrElse Future.successful(forbiddenAction(s"User does not have $agentEnrolmentId enrolment"))
      } recover authExceptionAction
  }

  private def forbiddenAction(reason: String): Result = {
    logger.debug(s"[AuthorisedSubmitVatReturn][async] - Forbidden access to vat-returns service. $reason")
    Forbidden(Json.toJson(Error(FORBIDDEN.toString, s"Forbidden access to vat-returns service. $reason")))
  }

  private def authExceptionAction: PartialFunction[Throwable, Result] = {
    case _: NoActiveSession =>
      logger.debug(s"[AuthorisedSubmitVatReturn][async] - User has no active session")
      Unauthorized(Json.toJson(Error(UNAUTHORIZED.toString, "User has no active session")))
    case ex: AuthorisationException => forbiddenAction(ex.reason)
  }
}
