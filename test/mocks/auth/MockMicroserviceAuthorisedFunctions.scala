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

package mocks.auth

import auth.MicroserviceAuthorisedFunctions
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Suite}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockMicroserviceAuthorisedFunctions extends BeforeAndAfterEach with MockitoSugar {
  self: Suite =>

  val mockAuth: MicroserviceAuthorisedFunctions = mock[MicroserviceAuthorisedFunctions]

  val authSuccessWithExternalId: Option[String] = Option("id")
  val authSuccessNoExternalId: None.type = None

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuth)
    setupMockAuthRetrievalSuccess(authSuccessWithExternalId)
  }

  def setupMockAuthRetrievalSuccess[X](retrievalValue: X): Unit = {
    when(mockAuth.authorised())
      .thenReturn(
        new mockAuth.AuthorisedFunction(EmptyPredicate) {
          override def retrieve[A](retrieval: Retrieval[A]): mockAuth.AuthorisedFunctionWithResult[A] {
            def apply[B](body: A => Future[B])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[B]
          } = new mockAuth.AuthorisedFunctionWithResult[A](EmptyPredicate, retrieval) {
            override def apply[B](body: A => Future[B])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[B] =
              body.apply(retrievalValue.asInstanceOf[A])
          }
        })
  }

  def setupMockAuthorisationException(exception: AuthorisationException = new InvalidBearerToken): Unit =
    when(mockAuth.authorised())
      .thenReturn(
        new mockAuth.AuthorisedFunction(EmptyPredicate) {
          override def apply[A](body: => Future[A])(implicit hc: HeaderCarrier, executionContext: ExecutionContext): Future[Nothing] =
            Future.failed(exception)

          override def retrieve[A](retrieval: Retrieval[A]): mockAuth.AuthorisedFunctionWithResult[A] {
            def apply[B](body: A => Future[B])(implicit hc: HeaderCarrier, executionContext: ExecutionContext): Future[B]
          } = new mockAuth.AuthorisedFunctionWithResult[A](EmptyPredicate, retrieval) {
            override def apply[B](body: A => Future[B])(implicit hc: HeaderCarrier, executionContext: ExecutionContext): Future[B] =
              Future.failed(exception)
          }
        })
}
