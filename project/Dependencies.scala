import sbt.*

object Dependencies {

  lazy val catsV                = "2.13.0"
  lazy val scalaTestV           = "3.2.19"
  lazy val scalaTestPlusCheckV  = "3.2.11.0"
  lazy val scalacheckV          = "1.18.1"
  lazy val testcontainersScalaV = "0.43.0"
  lazy val testcontainersV      = "1.21.3"

  // Typelevel
  val cats = "org.typelevel" %% "cats-core" % catsV

  // Test
  lazy val scalaTest          = "org.scalatest"     %% "scalatest"       % scalaTestV
  lazy val scalaTestPlusCheck = "org.scalatestplus" %% "scalacheck-1-15" % scalaTestPlusCheckV
  lazy val scalacheck         = "org.scalacheck"    %% "scalacheck"      % scalacheckV
  lazy val testcontainers     = "org.testcontainers" % "testcontainers"  % testcontainersV
  lazy val testcontainersScalaScalatest  = "com.dimafeng" %% "testcontainers-scala-scalatest"  % testcontainersScalaV
}
