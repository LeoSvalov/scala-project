import Dependencies._

ThisBuild / scalaVersion     := "2.13.6"
ThisBuild / version          := "0.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "scala-project",
    libraryDependencies += scalaTest % Test
  )
