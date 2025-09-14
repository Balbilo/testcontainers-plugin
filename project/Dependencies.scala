import sbt.*

object Dependencies {

  lazy val catsV                    = "3.6.3"
  lazy val http4sV                  = "0.23.30"
  lazy val smithy4sV                = "0.18.42"
  lazy val zioV                     = "2.1.21"
  lazy val zioConfigV               = "4.0.4"
  lazy val zioInteropCatsV          = "23.1.0.5"
  lazy val zioLoggingV              = "2.5.1"
  lazy val scalatestV               = "3.2.19"
  lazy val scalatestplusScalacheckV = "3.2.11.0"
  lazy val scalacheckV              = "1.18.1"
  lazy val testcontainersScalaV     = "0.43.0"
  lazy val testcontainersV          = "1.21.3"
  lazy val jacksonDataformatYamlV   = "2.19.2"
  lazy val logbackV                 = "1.2.13"
  lazy val julToSlf4jV              = "1.7.36"
  lazy val slf4jApiV                = "1.7.36"
  lazy val circeV                   = "0.14.14"

  // Typelevel
  lazy val catsCore         = "org.typelevel" %% "cats-core"          % catsV
  lazy val catsEffectKernel = "org.typelevel" %% "cats-effect-kernel" % catsV

  // Test
  lazy val scalatest               = "org.scalatest"     %% "scalatest"                      % scalatestV
  lazy val scalaTestPlusCheck      = "org.scalatestplus" %% "scalacheck-1-15"                % scalatestplusScalacheckV
  lazy val scalacheck              = "org.scalacheck"    %% "scalacheck"                     % scalacheckV
  lazy val testcontainers          = "org.testcontainers" % "testcontainers"                 % testcontainersV
  lazy val testcontainersScalaCore = "com.dimafeng"      %% "testcontainers-scala-core"      % testcontainersScalaV
  lazy val testcontainersScalatest = "com.dimafeng"      %% "testcontainers-scala-scalatest" % testcontainersScalaV

  // ZIO
  lazy val zio               = "dev.zio" %% "zio"                 % zioV
  lazy val zioConfig         = "dev.zio" %% "zio-config"          % zioConfigV
  lazy val zioConfigMagnolia = "dev.zio" %% "zio-config-magnolia" % zioConfigV
  lazy val zioConfigTypesafe = "dev.zio" %% "zio-config-typesafe" % zioConfigV
  lazy val zioInteropCats    = "dev.zio" %% "zio-interop-cats"    % zioInteropCatsV
  lazy val zioLogging        = "dev.zio" %% "zio-logging"         % zioLoggingV
  lazy val zioLoggingSL4J    = "dev.zio" %% "zio-logging-slf4j"   % zioLoggingV

  // Logging
  lazy val logback    = "ch.qos.logback" % "logback-classic" % logbackV
  lazy val julToSlf4j = "org.slf4j"      % "jul-to-slf4j"    % julToSlf4jV
  lazy val slf4jApi   = "org.slf4j"      % "slf4j-api"       % slf4jApiV

  // YAML
  lazy val jacksonDataformatYaml =
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % jacksonDataformatYamlV

  // Circe
  lazy val circeCore    = "io.circe" %% "circe-core"    % circeV
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeV
  lazy val circeParser  = "io.circe" %% "circe-parser"  % circeV

  // HTTP4s
  lazy val http4sDsl         = "org.http4s" %% "http4s-dsl"          % http4sV
  lazy val http4sEmberServer = "org.http4s" %% "http4s-ember-server" % http4sV
  lazy val http4sEmberClient = "org.http4s" %% "http4s-ember-client" % http4sV
  lazy val http4sCirce       = "org.http4s" %% "http4s-circe"        % http4sV

  lazy val smithy4sHttp4s = "com.disneystreaming.smithy4s" %% "smithy4s-http4s" % smithy4sV
}
