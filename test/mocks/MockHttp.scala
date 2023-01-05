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

package mocks

import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.{HttpClient, HttpResponse}

import scala.concurrent.Future

trait MockHttp extends AnyWordSpecLike with Matchers with OptionValues with MockitoSugar with BeforeAndAfterEach {

  val mockHttpGet: HttpClient = mock[HttpClient]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockHttpGet)
  }

  def setupMockHttpGet[A](url: String)(response: A): OngoingStubbing[Future[A]] =
    when(mockHttpGet.GET[A](ArgumentMatchers.eq(url), any(), any())(any(), any(), any()))
      .thenReturn(Future.successful(response))

  def setupMockHttpGet[A](url: String, queryParams: Seq[(String, String)])(response: A): OngoingStubbing[Future[A]] =
    when(mockHttpGet.GET[A](ArgumentMatchers.eq(url), ArgumentMatchers.eq(queryParams), any())(any(),
      any(), any())).thenReturn(Future.successful(response))

  def setupMockFailedHttpGet[A](url: String)(response: HttpResponse): OngoingStubbing[Future[A]] =
    when(mockHttpGet.GET[A](ArgumentMatchers.eq(url))(any(), any(), any())).thenReturn(Future.failed(new Exception))
}
