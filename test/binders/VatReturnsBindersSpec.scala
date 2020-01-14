/*
 * Copyright 2020 HM Revenue & Customs
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

package binders

import base.SpecBase
import models.VatReturnFilters
import VatReturnFilters._

class VatReturnsBindersSpec extends SpecBase {

  "The VatReturnsBinders.vatReturnsQueryBinder.bind method" should {

    "If no QueryParameters are passed" should {

      val queryParams: Map[String, Seq[String]] = Map("" -> Seq.empty)

      "return a None VatReturnFilters instance" in {

        val expected = None
        val actual = VatReturnsBinders.vatReturnsQueryBinder.bind("", queryParams)

        actual shouldBe expected
      }
    }

    "If a period-key query parameter is passed" which {

      "is formatted correctly as a non final obligation" should {

        val queryParams: Map[String, Seq[String]] = Map(periodKeyValue -> Seq("17AA"))

        "return an VatReturnFilters instance with correct parameters" in {

          val expected: Option[Right[Nothing, VatReturnFilters]] = Some(Right(VatReturnFilters(
            periodKey = "17AA"
          )))
          val actual: Option[Either[String, VatReturnFilters]] =
            VatReturnsBinders.vatReturnsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "is formatted correctly as a final obligation" should {

        val queryParams: Map[String, Seq[String]] = Map(periodKeyValue -> Seq("9999"))

        "return an VatReturnFilters instance with correct parameters" in {

          val expected: Option[Right[Nothing, VatReturnFilters]] = Some(Right(VatReturnFilters(
            periodKey = "9999"
          )))
          val actual: Option[Either[String, VatReturnFilters]] =
            VatReturnsBinders.vatReturnsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }

      "is formatted incorrectly" should {

        val queryParams: Map[String, Seq[String]] = Map(periodKeyValue -> Seq("BadPeriodKey"))

        "return a bad request error message with details of the error" in {

          val expected: Option[Left[String, Nothing]] =
            Some(Left(s"Failed to bind '$periodKeyValue=BadPeriodKey'."))
          val actual: Option[Either[String, VatReturnFilters]] =
            VatReturnsBinders.vatReturnsQueryBinder.bind("", queryParams)

          actual shouldBe expected
        }
      }
    }
  }
}
