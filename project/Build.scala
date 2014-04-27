import sbt._
import Keys._

object ProjectBuild extends Build {

  override lazy val settings = super.settings ++
  Seq(scalaVersion := "2.10.4", resolvers := Seq())

  lazy val root = Project(
    id = "TemplatesToTables",
    base = file("."),
    settings = Project.defaultSettings
    )
}
