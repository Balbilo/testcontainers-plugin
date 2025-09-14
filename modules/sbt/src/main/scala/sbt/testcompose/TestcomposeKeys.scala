package sbt.testcompose

import sbt.*
import sbt.Defaults.testOnlyParser
import sbt.Keys.*
import sjsonnew.*

import scala.Console
import scala.Console.*
import scala.sys.process.*
import scala.util.control.Exception.allCatch

trait TestcomposeKeys {

  // This is needed for the testOnlyParser for parsing input. Even though for our case is not going to be used.
  private object JsonFormatters extends CollectionFormats with PrimitiveFormats with AdditionalFormats
  import JsonFormatters.*

  private val testcomposeDisabled   = false
  private val testcomposeEnabled    = true
  private val dockerRepositoryKey   = "DOCKER_REPOSITORY"
  private val dockerImageVersionKey = "VERSION"

  lazy val testcomposePluginPrompt = settingKey[Unit]("plugin on loading message")

  lazy val dockerComposeUp                = taskKey[Unit]("docker compose up")
  lazy val dockerPs                       = taskKey[Unit]("docker ps")
  lazy val dockerComposeRestart           = taskKey[Unit]("docker compose restart")
  lazy val dockerComposeDown              = taskKey[Unit]("docker compose down")
  lazy val dockerImageCreation            = taskKey[Unit]("creates docker images needed for the test to run")
  lazy val dockerComposeTest              = taskKey[Unit]("docker compose test")
  lazy val dockerRepositoryEnv            = settingKey[Option[String]]("docker repository for images published")
  lazy val dockerImageVersionEnv          = settingKey[Option[String]]("docker image version published locally")
  lazy val dockerComposeFileEnv           = settingKey[Map[String, String]]("additional docker compose file variables")
  lazy val dockerDisableTestcomposePrompt = settingKey[Boolean]("helper table prompt with commands available")

  lazy val it     = taskKey[Unit]("integrated test against prepared environment (similar to test)")
  lazy val itOnly = inputKey[Unit]("integrated testOnly against prepared environment (similar to testOnly)")

  lazy val dockerComposeFile =
    settingKey[File]("docker compose file path default to project baseDirectory/compose.yaml e.g. it/compose.yaml")

  private[testcompose] def nothingTask    = Def.task(())
  private[testcompose] def nothingSetting = Def.setting(())

  private[testcompose] def dockerComposeUpTask = Def.taskDyn {
    Def.sequential(
      buildDockerImageTask,
      dockerComposeUpCmdTask,
      dockerPsTask(),
      showAvailableCommandsTask(),
    )
  }

  private[testcompose] def dockerComposeRestartTask = Def.taskDyn {
    Def.sequential(
      dockerComposeDown,
      buildDockerImageTask,
      dockerComposeUp,
    )
  }

  private[testcompose] def dockerComposeDownTask = Def.taskDyn {
    Def
      .sequential(
        dockerComposeDownCmdTask,
        dockerPsTask(),
      )
  }

  private[testcompose] def dockerPsTask(colour: String = GREEN) = Def.task {
    val dockerPs = Seq("docker", "ps", "--format", "table |  {{.ID}}\t|  {{.Names}}\t|  {{.Status}}\t|  {{.Ports}}\t|")
    allCatch.either {
      val output        = dockerPs.!!
      val lastPipeIndex = output.split("\n").head.trim.length
      val border        = "-" * lastPipeIndex
      Console.out.println(s"${RESET}${colour}$border\n$output$border${RESET}")
    }.left
      .map(e =>
        new MessageOnlyException(
          s"Failed to execute dockerPs task check if docker is running. Failed with: ${e.getMessage}"
        )
      )
      .toTry
      .get
  }

  private[testcompose] def itTask = Def.taskDyn {
    val testOptions = buildTestOptions(testcomposeDisabled).value
    (Test / testOnly).toTask(s" * -- ${testOptions.mkString(" ")}")
  }

  private[testcompose] def itOnlyTask = {
    // Using the same parser that testOnly use
    val parser = loadForParser(Test / definedTestNames)((s, i) => testOnlyParser(s, i getOrElse Nil))
    Def.inputTaskDyn {
      val (classes, argOptions) = parser.parsed
      val testOptions           = buildTestOptions(testcomposeDisabled).value
      val allOptions            = testOptions ++ argOptions

      (Test / testOnly)
        .toTask(s" ${classes.mkString(" ")} -- ${allOptions.mkString(" ")} ")
    }
  }

  private[testcompose] def testTask = Def.taskDyn {
    val testOptions = buildTestOptions(testcomposeEnabled).value
    Def
      .sequential(
        buildDockerImageTask,
        (Test / testOnly).toTask(s" * -- ${testOptions.mkString(" ")}"),
      )
  }

  private[testcompose] def testOnlyTask = {
    // Using the same parser that testOnly use
    val parser = loadForParser(Test / definedTestNames)((s, i) => testOnlyParser(s, i getOrElse Nil))
    Def.inputTaskDyn {
      val (classes, argOptions) = parser.parsed
      val testOptions           = buildTestOptions(testcomposeEnabled).value
      val allOptions            = testOptions ++ argOptions

      Def.sequential(
        buildDockerImageTask,
        (Test / testOnly)
          .toTask(s" ${classes.mkString(" ")} -- ${allOptions.mkString(" ")} "),
      )
    }
  }

  private def dockerComposeUpCmdTask = Def.task {
    val dockerComposeEnv     = getDockerComposeEnv.value
    val dockerComposeEnvFile = getEnvFile.value
    val dockerComposeFileV   = dockerComposeFile.value

    val dockerComposeUp =
      Seq(
        "docker",
        "compose",
        "--env-file",
        dockerComposeEnvFile.getPath,
        "-f",
        dockerComposeFileV.getPath,
        "up",
        "-d",
        "--wait",
      )

    allCatch.either {
      writeDockerComposeEnv(dockerComposeEnvFile, dockerComposeEnv)
      dockerComposeUp.!!
    }.left
      .map(e =>
        new MessageOnlyException(
          s"Failed to execute dockerComposeUp task check if docker is running. Failed with: ${e.getMessage}"
        )
      )
      .toTry
      .get
  }

  private def dockerComposeDownCmdTask = Def.task {
    val dockerComposeFileV   = dockerComposeFile.value
    val dockerComposeEnvFile = getEnvFile.value

    allCatch
      .either(
        Seq(
          "docker",
          "compose",
          "--env-file",
          dockerComposeEnvFile.getPath,
          "-f",
          dockerComposeFileV.getPath,
          "down",
        ).!!
      )
      .left
      .map(e =>
        new MessageOnlyException(
          s"Failed to execute dockerComposeDown task check if docker is running. Failed with: ${e.getMessage}"
        )
      )
      .toTry
      .get
  }

  private[testcompose] def dockerComposeTestTask = Def.taskDyn {
    Def.sequential(
      buildDockerImageTask,
      dockerComposeUpCmdTask,
      dockerPsTask(),
      itTask,
      dockerComposeDownCmdTask,
      dockerPsTask(),
    )
  }

  /** Write the environment variables to a .env file for docker compose. More details:
    * https://docs.docker.com/compose/how-tos/environment-variables/variable-interpolation/#substitute-with---env-file
    *
    * @param envFile
    *   The file to write the environment variables to
    * @param dockerComposeEnv
    *   The environment variables to resolve in docker compose file
    */
  private[testcompose] def writeDockerComposeEnv(envFile: File, dockerComposeEnv: Seq[String]) =
    IO.write(envFile, dockerComposeEnv.mkString("\n"))

  private[testcompose] def bootstrapPluginPrompt(colour: String = GREEN) = Def.setting {
    if (dockerDisableTestcomposePrompt.value) ()
    else {
      val message =
        """
          || sbt-testcompose                                                            |
          ||----------------------------------------------------------------------------|
          || Commands             | Description                                         |
          ||----------------------|-----------------------------------------------------|
          || dockerComposeUp      | docker compose up command                           |
          || dockerPs             | docker ps command                                   |
          || dockerComposeDown    | docker compose down command                         |
          || dockerComposeRestart | Restart docker compose, build new image             |
          || dockerComposeTest    | docker compose test command                         |
          || it                   | Test against prepared environment                   |
          || itOnly               | Test specifying suite against prepared environment  |""".stripMargin

      val longestLine = message.split("\n").max
      val border      = "-" * longestLine.length

      Console.out.println(s"${RESET}${colour}$border\n${message.tail}\n$border${RESET}")
    }
  }

  private def buildTestOptions(useTestContainers: Boolean) = Def.task {
    val dockerComposeFilePath = dockerComposeFile.value.getPath
    val dockerComposeEnv      = getDockerComposeEnv.value
    Seq(
      s"-DuseTestcontainers=$useTestContainers",
      s"-DdockerComposeFile=$dockerComposeFilePath",
      s"-DdockerComposeEnv=${dockerComposeEnv.mkString(";")}",
    )
  }

  private def getEnvFile = Def.task(target.value / ".env.local")

  private def getDockerComposeEnv: Def.Initialize[Task[Seq[String]]] = Def.task {
    val dockerRepositoryEnvV   = dockerRepositoryEnv.value
    val dockerComposeFileEnvV  = dockerComposeFileEnv.value
    val dockerImageVersionEnvV = dockerImageVersionEnv.value.getOrElse(version.value)

    val dockerRepository               = dockerRepositoryEnvV.map(dr => s"$dockerRepositoryKey=$dr")
    val additionalDockerComposeFileEnv = dockerComposeFileEnvV.map { case (k, v) => s"$k=$v" }

    Seq(s"$dockerImageVersionKey=$dockerImageVersionEnvV") ++
      dockerRepository.toSeq ++
      additionalDockerComposeFileEnv
  }

  private def showAvailableCommandsTask(colour: String = YELLOW) = Def.task {
    val message =
      s"""
         ||  AVAILABLE COMMANDS       | DESCRIPTION                                        |
         ||  it                       | All tests against prepared environment.            |
         ||  itOnly                   | Specified tests only against prepared environment. |""".stripMargin

    // TODO: Hardcode borders
    val longestLine = message.split("\n").max
    val border      = "-" * longestLine.length

    Console.out.println(s"${RESET}${colour}$border\n${message.tail}\n$border${RESET}")
  }

  private def buildDockerImageTask = Def.task {
    allCatch
      .either(dockerImageCreation.value)
      .left
      .map(e =>
        new MessageOnlyException(
          s"Failed to execute dockerImageCreation task check if docker is running. Failed with: ${e.getMessage}"
        )
      )
      .toTry
      .get
  }
}
