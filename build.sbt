import Projects.*

val enableScalaLint = sys.env.getOrElse("ENABLE_SCALA_LINT_ON_COMPILE", "true").toBoolean

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion              := "3.3.6"
ThisBuild / organization              := "io.testcompose"
ThisBuild / organizationName          := "testcompose"
ThisBuild / scalafixOnCompile         := enableScalaLint
ThisBuild / scalafmtOnCompile         := enableScalaLint
ThisBuild / semanticdbVersion         := scalafixSemanticdb.revision
ThisBuild / semanticdbEnabled         := true
ThisBuild / Test / fork               := true
ThisBuild / run / fork                := true
ThisBuild / Test / parallelExecution  := true
ThisBuild / Test / testForkedParallel := true

lazy val modulesDirName  = "modules"
lazy val testcomposeName = "testcompose"
lazy val examplesName    = "examples"

def createTestcomposeModule(moduleName: String): Project = {
  val moduleFullName = s"$testcomposeName-$moduleName"
  Project(moduleFullName, file(s"$modulesDirName/$moduleName"))
}

def createExamplesModule(moduleName: String): Project = {
  val moduleFullName = s"$examplesName-$moduleName"
  Project(moduleFullName, file(s"$examplesName/$moduleName"))
}

lazy val root = Project(testcomposeName, file("."))
  .settings(Aliases.all)

lazy val testcomposeModules = Project(modulesDirName, file(modulesDirName))
  .aggregate(sbtModule, testcomposeCore)

lazy val sbtModule = Project("sbt-testcompose", file(s"$modulesDirName/sbt"))
//  .enablePlugins(BuildInfoPlugin)
//  .settings(buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion))
  .settings(scalaVersion := "2.12.20")
  .settings(sbtPlugin := true)

lazy val testcomposeCore = createTestcomposeModule("core")
  .withDependencies(
    Dependencies.testcontainers,
    Dependencies.testcontainersScalaCore,
    Dependencies.catsEffectKernel,
    Dependencies.jacksonDataformatYaml,
  )

lazy val testcomposeZIO = createTestcomposeModule("zio")
  .dependsOn(testcomposeCore)
  .withDependencies(
    Dependencies.zio
  )

// EXAMPLES
lazy val examplesModules = Project(examplesName, file(examplesName))

lazy val examplesZIOScalatest = createExamplesModule("zio-scalatest")
  .enablePlugins(TestcomposePlugin)
  .dependsOn(testcomposeZIO)
  .withDependencies(
    Dependencies.zio,
    Dependencies.testcontainers,
    Dependencies.testcontainersScalaCore,
    Dependencies.catsEffectKernel,
    Dependencies.jacksonDataformatYaml,
  )

//
//lazy val backendTestKitModule = createBackendModule("test-kit")(None)
//  .dependsOn(backendDomainModule)
//  .withDependencies(
//    Dependencies.zio,
//    Dependencies.zioConfig,
//    Dependencies.zioConfigTypesafe,
//    Dependencies.zioLogging,
//    Dependencies.zioLoggingSL4J,
//    Dependencies.zioInteropCats,
//    Dependencies.scalaTest,
//    Dependencies.scalacheck,
//    Dependencies.scalaTestPlusCheck,
//    Dependencies.testcontainers,
//    Dependencies.testcontainersScalaScalatest,
//    Dependencies.chimney,
//  )
//
//lazy val backendPostgreSQLTestModule = createBackendModule("postgresql-test")(None)
//  .dependsOn(backendTestKitModule)
//  .withDependencies(
//    Dependencies.testcontainersScalaPostgresql,
//    Dependencies.doobieCore,
//    Dependencies.doobieHikari,
//    Dependencies.doobiePostgres,
//    Dependencies.doobieTranzactio,
//    Dependencies.hikariCP,
//  )
//
//lazy val backendSchemas = createBackendModule("schemas")(None)
//
//// Gateway
//lazy val createBackendGatewayModule = createBackendModule("gateway") _
//
//lazy val backendGatewayRoot = createBackendGatewayModule(None)
//  .aggregate(backendGatewayCore, backendGatewayIt)
//
//lazy val backendGatewayCore = createBackendGatewayModule(Some("core"))
//  .enablePlugins(Smithy4sCodegenPlugin)
//  .enablePlugins(DockerPlugin)
//  .dependsOn(backendDomainModule)
//  .dependsOn(backendClockModule)
//  .dependsOn(backendTestKitModule % Test)
//  .dependsOn(backendPostgreSQLTestModule % Test)
//  .settings(Docker.settings(docker, Compile))
//  .withDependencies(
//    Dependencies.zio,
//    Dependencies.zioConfig,
//    Dependencies.zioConfigMagnolia,
//    Dependencies.zioConfigTypesafe,
//    Dependencies.zioLogging,
//    Dependencies.zioLoggingSL4J,
//    Dependencies.zioInteropCats,
//    Dependencies.smithy4sHttp4s,
//    Dependencies.http4sDsl,
//    Dependencies.http4sEmberServer,
//    Dependencies.pureconfig,
//    Dependencies.pureconfigCats,
//    Dependencies.pureconfigCatsEffect,
//    Dependencies.julToSlf4j,
//    Dependencies.logback,
//    Dependencies.chimney,
//    Dependencies.doobieCore,
//    Dependencies.doobieHikari,
//    Dependencies.doobiePostgres,
//    Dependencies.doobieTranzactio,
//    Dependencies.hikariCP,
//  )
//
//lazy val backendGatewayIt = createBackendGatewayModule(Some("it"))
//  .dependsOn(backendGatewayCore % Test)
//  .dependsOn(backendTestKitModule % Test)
//  .dependsOn(backendPostgreSQLTestModule % Test)
//  .withDependencies(
//    Dependencies.http4sEmberClient,
//    Dependencies.http4sCirce,
//    Dependencies.ironCirce,
//    Dependencies.circeCore,
//    Dependencies.circeGeneric,
//    Dependencies.circeParser,
//  )
//  .settings(
//    test := {
//      val testResult = (backendGatewayCore / docker).value
//      (Test / test).value
//    }
//  )
