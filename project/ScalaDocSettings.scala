import com.typesafe.sbt.sbtghpages.GhpagesPlugin.autoImport._
import com.typesafe.sbt.site.SitePlugin.autoImport.makeSite
import com.typesafe.sbt.SbtGit.GitKeys.{gitBranch, gitRemoteRepo}

import sbt.Keys._
import sbt._

object ScalaDocSettings {

  val settings = Seq(
    ghpagesPushSite := (ghpagesPushSite dependsOn makeSite).value,
    ghpagesNoJekyll := false,
    gitRemoteRepo := "git@github.com:aldemirenes/snowplow-scala-analytics-sdk.git",
    gitBranch := Some("gh-pages"),
    excludeFilter in ghpagesCleanSite := "index.html"
  )
}

