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

package mocks.services

import connectors.httpParsers.VatReturnsHttpParser.HttpGetResult
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import models._
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar
import services.VatReturnsService

import scala.concurrent.Future

trait MockVatReturnsService extends AnyWordSpecLike with Matchers with OptionValues with MockitoSugar with BeforeAndAfterEach {

  val mockVatReturnsService: VatReturnsService = mock[VatReturnsService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockVatReturnsService)
  }

  def setupMockGetVatReturns(vrn: String, queryParameters: VatReturnFilters)
                                (response: HttpGetResult[VatReturnDetail]): OngoingStubbing[Future[HttpGetResult[VatReturnDetail]]] =
    when(
      mockVatReturnsService.getVatReturns(
        ArgumentMatchers.eq(vrn),
        ArgumentMatchers.eq(queryParameters)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))

  def setupMockSubmitReturn(vrn: String, model: VatReturnDetail, originatorID: String)
                           (response: HttpGetResult[SuccessModel]): OngoingStubbing[Future[HttpGetResult[SuccessModel]]] =
    when(
      mockVatReturnsService.submitVatReturn(
        ArgumentMatchers.eq(vrn),
        ArgumentMatchers.eq(model),
        ArgumentMatchers.eq(originatorID)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
}
