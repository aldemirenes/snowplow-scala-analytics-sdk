import scoverage.ScoverageKeys._
import sbt.Keys._
import sbt._

object ScoverageSettings {

  val settings = Seq(
    coverageEnabled := true,
    coverageMinimum := 80,
    coverageFailOnMinimum := true,
    coverageHighlighting := false,
    (test in Test) := {
      (coverageReport dependsOn (test in Test)).value
    }
  )
}
