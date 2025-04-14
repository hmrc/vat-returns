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

import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.*
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin

val appName = "vat-returns"

lazy val appDependencies: Seq[ModuleID] = compile ++ test()

lazy val coverageSettings: Seq[Setting[?]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    ".*Reverse.*",
    "app.*",
    "prod.*",
    "config.*",
    "testOnlyDoNotUseInAppConf.*"
  )

  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    ScoverageKeys.coverageFailOnMinimum    := true,
    ScoverageKeys.coverageHighlighting     := true
  )
}

val playVersion      = "play-30"
val bootstrapVersion = "8.6.0"

val compile = Seq(
  "uk.gov.hmrc" %% s"bootstrap-backend-$playVersion" % bootstrapVersion
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc"               %% s"bootstrap-test-$playVersion" % bootstrapVersion,
  "org.scalatestplus"         %% "mockito-3-4"                  % "3.2.10.0",
  "com.github.java-json-tools" % "json-schema-validator"        % "2.2.14"
).map(_ % scope)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(coverageSettings *)
  .settings(scalaSettings *)
  .settings(PlayKeys.playDefaultPort := 9157)
  .settings(defaultSettings() *)
  .settings(majorVersion := 0)
  .settings(
    scalaVersion := "2.13.16",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    routesImport += "binders.VatReturnsBinders._",
    scalacOptions ++= Seq("-Wconf:cat=unused-imports&src=.*routes.*:s", "-Wconf:cat=unused&src=routes/.*:s")
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings) *)
  .settings(
    IntegrationTest / Keys.fork                  := false,
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it")).value,
    IntegrationTest / resourceDirectory          := (baseDirectory apply { baseDir: File => baseDir / "it/resources" }).value,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    IntegrationTest / parallelExecution := false
  )
