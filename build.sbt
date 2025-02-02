/*
 * Copyright (c) 2016-2019 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

lazy val root = project.in(file("."))
  .settings(Seq[Setting[_]](
    name               := "snowplow-scala-analytics-sdk",
    organization       := "com.snowplowanalytics",
    version            := "1.0.0-M1",
    description        := "Scala analytics SDK for Snowplow",
    scalaVersion       := "2.12.8",
    crossScalaVersions := Seq("2.11.12", "2.12.8")
  ))
  .settings(BuildSettings.buildSettings)
  .settings(BuildSettings.publishSettings)
  .settings(MimaSettings.mimaSettings)
  .settings(ScoverageSettings.settings)
  .settings(ScalaDocSettings.settings)
  .enablePlugins(SiteScaladocPlugin)
  .enablePlugins(GhpagesPlugin)
  .settings(Seq(
    shellPrompt := { _ => name.value + " > " }
  ))
  .settings(
    libraryDependencies ++= Seq(
      // Scala
      Dependencies.igluCore,
      Dependencies.cats,
      Dependencies.circeParser,
      Dependencies.circeGeneric,
      Dependencies.circeJava,
      Dependencies.s3,
      Dependencies.dynamodb,
      // Scala (test only)
      Dependencies.specs2
    )
  )
