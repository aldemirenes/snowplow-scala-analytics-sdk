import com.typesafe.tools.mima.plugin.MimaKeys._
import com.typesafe.tools.mima.plugin.MimaPlugin
import sbt.Keys._
import sbt._

object MimaSettings {

  // If new version introduces breaking changes,
  // clear-out mimaBinaryIssueFilters and mimaPreviousVersions.
  // Otherwise, add previous version to set without
  // removing other versions.
  val mimaPreviousVersions = Set()

  val mimaSettings = MimaPlugin.mimaDefaultSettings ++ Seq(
      mimaPreviousArtifacts := mimaPreviousVersions.map { organization.value %% name.value % _ },
      mimaBinaryIssueFilters ++= Seq(),
      test in Test := {
        mimaReportBinaryIssues.value
        (test in Test).value
      }
    )
}