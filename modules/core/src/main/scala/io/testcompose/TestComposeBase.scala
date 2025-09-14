package io.testcompose

import com.dimafeng.testcontainers.{ContainerDef, DockerComposeContainer, ExposedService, ServiceLogConsumer}
import org.slf4j.LoggerFactory
import org.testcontainers.containers.output.Slf4jLogConsumer

import java.io.File
import scala.collection.mutable

/** Test base trait that allows users to run tests using testcontainers or an against already running environment.
  *
  * @tparam F[_]
  *   The effect type used in the context
  * @tparam Context
  *   The Context provided to each test
  */
private[testcompose] trait TestComposeBase[F[_], Context] { self =>

  final protected val defaultDockerComposePath = "compose.yaml"

  type Containers = DockerComposeContainer | RemoteTest

  private val dockerComposeFileConf: String = defaultDockerComposePath
  private val useTestcontainers: Boolean    = true

  private val dockerComposeEnv: mutable.Map[String, String] = mutable.Map.empty

  def exposedServices: Set[ExposedService] = Set.empty
  def logConsumers: Set[String]            = Set.empty

  /** This method facilitates passing a config to each test, users are required to implement a config-building
    *
    * @return
    *   Resource[F, Config]
    */
//  def configLoader: Resource[F, Config]

  /** This method facilitates passing a context to each test, users are required to implement a context-building
    * mechanism using containers from testcontainers.
    *
    * @return
    *   Resource[F, Context]
    */
//  def resourceContext: Resource[F, Context]

  def withContext[A](block: Context => F[A]): F[A]

  def withUnsafeContext[A](block: Context => A): A

  final def containerDef: ContainerDef =
    if (useTestcontainers) {
      val composeTempFile = TestComposeUtils.removePortsAndWriteTemp(
        new File(dockerComposeFileConf),
        self.getClass.getSimpleName.toLowerCase,
      )
      TestComposeContainer
        .Def(
          composeFiles = DockerComposeContainer
            .ComposeFile(Left(composeTempFile)),
          exposedServices = exposedServices.toSeq,
          logConsumers = logConsumers
            .map(serviceName =>
              ServiceLogConsumer(
                serviceName,
                new Slf4jLogConsumer(LoggerFactory.getLogger(self.getClass)).withPrefix(serviceName),
              )
            )
            .toSeq,
          env = dockerComposeEnv,
          tailChildContainers = true,
          afterContainersStop = LazyList(TestComposeUtils.deleteTemp(composeTempFile)),
        )
    } else RemoteTest.remoteTestContainerDef

//  /** Replacing test run method with a flexible function that could take more arguments this way we are decoupling run
//    * method from scalatest, providing our own implementation in the higher interfaces abstraction.
//    *
//    * @param testName
//    *   same as scalatest run method
//    * @param args
//    *   same as scalatest run method
//    * @param useItTest
//    *   run Integrated test
//    * @return
//    */
//  final def runTestcontainers(
//      testName: Option[String],
//      dockerComposeFile: String,
//      dockerComposeEnv: Map[String, String],
//      useItTest: Boolean,
//  ): Status = {
//    dockerComposeFileConf = dockerComposeFile
//    useTestContainers = args.configMap.getOrElse("useTestcontainers", useTestContainers.toString).toString.toBoolean
//    dockerComposeEnv = args.configMap
//      .getOrElse("dockerComposeEnv", "")
//      .toString
//      .split(";")
//      .map(_.split("="))
//      .filter(_.length == 2)
//      .map(arr => arr.head -> arr.tail.head)
//      .toMap
//
//    if (useTestContainers || useItTest) super.run(testName, args)
//    else SucceededStatus
//  }
}

//final private[testcontainers] class NoopContainer extends Startable, Stoppable {
//  override def start(): Unit = ()
//  override def stop(): Unit  = ()
//}

//package com.sky.hades.common.testcontainers
//
//import java.io.File
//import com.dimafeng.testcontainers.*
//import com.dimafeng.testcontainers.scalatest.TestContainersForAll
//import org.scalatest.*
//import org.slf4j.LoggerFactory
//import org.testcontainers.containers.output.Slf4jLogConsumer
//
//import scala.jdk.OptionConverters.RichOptional

/** Flexible testcontainers trait that allows users to run tests using testcontainers or an against already running
  * environment.
  *
  * @tparam Context
  *   The Context provided to each test
  * @tparam Settings
  *   The settings provided to build Context
  */
//private[testcontainers] trait FlexTestContainers[Context, Settings] extends TestContainersForAll {
//  self: Suite =>
//
//  final override type Containers = FlexDockerComposeContainer | NoopContainer
//
//  protected final val defaultDockerComposePath = "compose.yaml"
//
//  private var useTestContainers = true
//  private var dockerComposeEnv  = Map.empty[String, String]
//
//  protected final var dockerComposeFileConf: String = defaultDockerComposePath
//
//  def exposedServices: Set[ExposedService] = Set.empty // Services to be exposed
//  def exposedKafkaServices: Set[ExposedKafkaService] =
//    Set.empty // Kafka Services to be exposed (!! Doesn't work with Kafka in KRAFT mode)
//  def logConsumers: Set[String] = Set.empty // Specify the ServiceName
//
//  /** This method facilitates passing a context to each test, users are required to implement a context-building
//   * mechanism using containers from testcontainers.
//   *
//   * @param settings
//   *   Settings
//   * @return
//   *   Resource[F, Context]
//   */
//  def resourceContext(settings: Settings): Resource[IO, Context]
//
//  def withContext[A](block: Context => IO[A]): IO[A]
//
//  def withUnsafeContext[A](block: Context => A): A
//
//  /** Replacing test run method with a flexible function that could take more arguments this way we are decoupling run
//   * method from scalatest, providing our own implementation in the higher interfaces abstraction.
//   *
//   * @param testName
//   *   same as scalatest run method
//   * @param args
//   *   same as scalatest run method
//   * @param useItTest
//   *   run Integrated test
//   * @return
//   */
//  final def runTestcontainers(testName: Option[String],
//                              args: Args,
//                              dockerComposeFile: String,
//                              useItTest: Boolean): Status = {
//    dockerComposeFileConf = dockerComposeFile
//    useTestContainers = args.configMap.getOrElse("useTestcontainers", useTestContainers.toString).toString.toBoolean
//    dockerComposeEnv = args.configMap
//      .getOrElse("dockerComposeEnv", "")
//      .toString
//      .split(";")
//      .map(_.split("="))
//      .filter(_.length == 2)
//      .map(arr => arr.head -> arr.tail.head)
//      .toMap
//
//    if (useTestContainers || useItTest) super.run(testName, args)
//    else SucceededStatus
//  }
//
//  abstract override def run(testName: Option[String], args: Args): Status =
//    runTestcontainers(testName, args, dockerComposeFileConf, useItTest = false)
//
//  final def containerDef: ContainerDef =
//    if (useTestContainers) {
//      val composeTempFile = DockerComposeFileUtils.removePortsAndWriteTemp(
//        new File(dockerComposeFileConf),
//        self.getClass.getSimpleName.toLowerCase
//      )
//
//      FlexDockerComposeContainer
//        .Def(
//          composeFiles = DockerComposeContainer
//            .ComposeFile(Left(composeTempFile)),
//          afterContainersStop = () => DockerComposeFileUtils.deleteTemp(composeTempFile),
//          exposedServices = exposedServices.toSeq ++ exposedKafkaServices.map(_.toExposedService).toSeq,
//          logConsumers = logConsumers
//            .map(serviceName =>
//              ServiceLogConsumer(
//                serviceName,
//                new Slf4jLogConsumer(LoggerFactory.getLogger(self.getClass)).withPrefix(serviceName)
//              ))
//            .toSeq,
//          env = dockerComposeEnv,
//          tailChildContainers = true
//        )
//    } else NoopContainer.noopContaineDef
//
//  // This is due to testcontainers docker compose limitation and its only specific to Kafka
//  // Kafka protocol requires to know the host and port that will be exposed for advertising the brokers to the clients
//  // Thus, Kafka advertised.listeners need to be dynamically updated after container
//  // start because of dynamic port assignment by Testcontainers in docker compose
//  // IMPORTANT: KRAFT mode is not supported due to limitation of modifying advertised.listeners after broker starts!
//  private def configureKafkaListeners(containers: DockerComposeContainer): Unit =
//    exposedKafkaServices.foreach { exposedKafkaService =>
//      val kafkaContainer = containers.container
//        .getContainerByServiceName(exposedKafkaService.name)
//        .toScala
//        .getOrElse(sys.error(s"Could not find Kafka container by name [${exposedKafkaService.name}]"))
//
//      val externalUrl =
//        s"${containers.getServiceHost(exposedKafkaService.name, exposedKafkaService.externalPort)}:${containers.getServicePort(exposedKafkaService.name, exposedKafkaService.externalPort)}"
//      val internalUrl =
//        s"${exposedKafkaService.name}:${exposedKafkaService.internalPort}"
//      val listenerUrl =
//        s"${exposedKafkaService.listenerHost.getOrElse("localhost")}:${exposedKafkaService.listenerPort.getOrElse(exposedKafkaService.internalPort)}"
//
//      val command =
//        "kafka-configs" :: "--bootstrap-server" :: s"$listenerUrl" :: "--entity-type" :: "brokers" :: "--entity-name" :: "1001" :: "--alter" :: "--add-config" :: s"advertised.listeners=[PLAINTEXT://$internalUrl,OUTSIDE://$externalUrl]" :: Nil
//
//      val commandResult = kafkaContainer.execInContainer(command*)
//
//      if (commandResult.getExitCode != 0) {
//        sys.error(
//          s"Failed to alter bootstrap urls of running Kafka container stdout [${commandResult.getStdout}] stderr [${commandResult.getStderr}]"
//        )
//      }
//    }
//
//  override def afterContainersStart(container: Containers): Unit = {
//    super.afterContainersStart(container)
//
//    container match {
//      case c: DockerComposeContainer => configureKafkaListeners(c)
//      case _: NoopContainer          => ()
//    }
//  }
//
//  final override def startContainers(): Containers =
//    containerDef.start().asInstanceOf[Containers]
//}
//
//package com.sky.hades.common.testcontainers
//
//import com.dimafeng.testcontainers.ContainerDef
//import com.dimafeng.testcontainers.lifecycle.Stoppable
//import org.testcontainers.lifecycle.Startable
//
///** NoopContainer used as non Startable, Stoppable container for integrated test.
// */
//private[testcontainers] final class NoopContainer extends Startable, Stoppable {
//  override def start(): Unit = ()
//  override def stop(): Unit  = ()
//}
//
//object NoopContainer {
//  lazy val noopContainer = new NoopContainer
//
//  lazy val noopContaineDef = new ContainerDef {
//    override type Container = NoopContainer
//
//    override def createContainer(): NoopContainer = noopContainer
//  }
//}
