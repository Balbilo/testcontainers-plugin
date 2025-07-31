import sbt.*

object Dependencies {

  lazy val catsV                  = "3.6.1"
  lazy val zioV                   = "2.1.19"
  lazy val scalaTestV             = "3.2.19"
  lazy val scalaTestPlusCheckV    = "3.2.11.0"
  lazy val scalacheckV            = "1.18.1"
  lazy val testcontainersScalaV   = "0.43.0"
  lazy val testcontainersV        = "1.21.3"
  lazy val jacksonDataformatYamlV = "2.19.1"

  // Typelevel
  lazy val catsCore         = "org.typelevel" %% "cats-core"          % catsV
  lazy val catsEffectKernel = "org.typelevel" %% "cats-effect-kernel" % catsV

  // Test
  lazy val scalaTest                    = "org.scalatest"     %% "scalatest"                      % scalaTestV
  lazy val scalaTestPlusCheck           = "org.scalatestplus" %% "scalacheck-1-15"                % scalaTestPlusCheckV
  lazy val scalacheck                   = "org.scalacheck"    %% "scalacheck"                     % scalacheckV
  lazy val testcontainers               = "org.testcontainers" % "testcontainers"                 % testcontainersV
  lazy val testcontainersScalaCore      = "com.dimafeng"      %% "testcontainers-scala-core"      % testcontainersScalaV
  lazy val testcontainersScalaScalatest = "com.dimafeng"      %% "testcontainers-scala-scalatest" % testcontainersScalaV

  // ZIO
  lazy val zio = "dev.zio" %% "zio" % zioV

  // YAML
  lazy val jacksonDataformatYaml =
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % jacksonDataformatYamlV
}
