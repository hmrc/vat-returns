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

import base.SpecBase
import mocks.auth.MockMicroserviceAuthorisedFunctions
import play.api.mvc.Results._
import play.api.mvc.{Action, AnyContent}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthActionImplSpec extends SpecBase with MockMicroserviceAuthorisedFunctions {

  object TestAuthActionImpl extends AuthActionImpl(mockAuth)
  def result: Action[AnyContent] = TestAuthActionImpl.async { _ => Future.successful(Ok("")) }

  "Auth Action" when {
    "the user is successfully logged in with an external ID provider" must {
      "return OK result" in {
        setupMockAuthRetrievalSuccess(authSuccessWithExternalId)
        status(result(fakeRequest)) shouldBe OK
      }
    }
    "the user is successfully logged with NO external ID provider" must {
      "return OK result" in {
        setupMockAuthRetrievalSuccess(authSuccessNoExternalId)
        status(result(fakeRequest)) shouldBe UNAUTHORIZED
      }
    }
    "the user hasn't logged in" must {
      "redirect the user to log in " in {
        setupMockAuthorisationException(new MissingBearerToken)
        status(result(fakeRequest)) shouldBe UNAUTHORIZED
      }
    }

    "the user's session has expired" must {
      "redirect the user to log in " in {
        setupMockAuthorisationException(new BearerTokenExpired)
        status(result(fakeRequest)) shouldBe UNAUTHORIZED
      }
    }

    "the user doesn't have sufficient enrolments" must {
      "redirect the user to the unauthorised page" in {
        setupMockAuthorisationException(new InsufficientEnrolments)
        status(result(fakeRequest)) shouldBe FORBIDDEN
      }
    }

    "the user doesn't have sufficient confidence level" must {
      "redirect the user to the unauthorised page" in {
        setupMockAuthorisationException(new InsufficientConfidenceLevel)
        status(result(fakeRequest)) shouldBe FORBIDDEN
      }
    }

    "the user used an unaccepted auth provider" must {
      "redirect the user to the unauthorised page" in {
        setupMockAuthorisationException(new UnsupportedAuthProvider)
        status(result(fakeRequest)) shouldBe FORBIDDEN
      }
    }

    "the user has an unsupported affinity group" must {
      "redirect the user to the unauthorised page" in {
        setupMockAuthorisationException(new UnsupportedAffinityGroup)
        status(result(fakeRequest)) shouldBe FORBIDDEN
      }
    }

    "the user has an unsupported credential role" must {
      "redirect the user to the unauthorised page" in {
        setupMockAuthorisationException(new UnsupportedCredentialRole)
        status(result(fakeRequest)) shouldBe FORBIDDEN
      }
    }
  }
}
