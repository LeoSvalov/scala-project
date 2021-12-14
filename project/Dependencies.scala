import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.8"
  lazy val sqllite = "org.xerial" % "sqlite-jdbc" % "3.36.0.3"
  lazy val doobie = Seq(
    "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
    "org.tpolecat" %% "doobie-h2" % "1.0.0-RC1",
    "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC1",
    "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC1",
    "org.tpolecat" %% "doobie-specs2" % "1.0.0-RC1" % "test",
    "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC1" % "test"
  )
  lazy val spark = Seq(
    "org.apache.spark" %% "spark-mllib" % "3.2.0",
    "org.apache.spark" %% "spark-sql" % "3.2.0",
    "org.apache.spark" %% "spark-core" % "3.2.0"
  )
  lazy val circe = "io.circe" %% "circe-generic" % "0.14.1"
  lazy val http4s = Seq(
    "org.http4s" %% "http4s-circe" % "1.0.0-M27",
    "org.http4s" %% "http4s-blaze-client" % "1.0.0-M27",
    "org.http4s" %% "http4s-blaze-server" % "1.0.0-M27",
    "org.http4s" %% "http4s-dsl" % "1.0.0-M27",
    "org.http4s" %% "rho-swagger" % "0.21.0-RC1"
  )

}
