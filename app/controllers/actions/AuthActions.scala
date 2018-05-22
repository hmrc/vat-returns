/*
 * Copyright 2018 HM Revenue & Customs
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

import javax.inject.Singleton
import auth.AuthenticatedRequest
import com.google.inject.Inject
import models.{ForbiddenError, UnauthenticatedError}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.Results.{Forbidden, Unauthorized}
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthActionImpl @Inject()(val authorisedFunctions: AuthorisedFunctions)(implicit ec: ExecutionContext) extends AuthAction {

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers)

    authorisedFunctions.authorised().retrieve(Retrievals.externalId) {
      case Some (externalId) => block(AuthenticatedRequest(request, externalId))

      case _ =>
        Logger.debug("[AuthActionImpl][invokeBlock] Did not retrieve externalID, returning Unauthorised - Unauthenticated Error")
        Future.successful(Unauthorized(Json.toJson(UnauthenticatedError)))

    } recover {
      case _: NoActiveSession =>
        Logger.debug("[AuthActionImpl][invokeBlock] Request did not have an Active Session, returning Unauthorised - Unauthenticated Error")
        Unauthorized(Json.toJson(UnauthenticatedError))

      case _ =>
        Logger.debug("[AuthActionImpl][invokeBlock] Request has an active session but was not authorised, returning Forbidden - Not Authorised Error")
        Forbidden(Json.toJson(ForbiddenError))
    }
  }
}

trait AuthAction extends ActionBuilder[AuthenticatedRequest] with ActionFunction[Request, AuthenticatedRequest]
