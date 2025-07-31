package io.testcompose

import com.dimafeng.testcontainers.*
import com.dimafeng.testcontainers.DockerComposeContainer.ComposeFile

import scala.collection.mutable

class TestComposeContainer(
    composeFiles: ComposeFile,
    exposedServices: Seq[ExposedService] = Seq.empty,
    identifier: String = DockerComposeContainer.randomIdentifier,
    scaledServices: Seq[ScaledService] = Seq.empty,
    pull: Boolean = true,
    localCompose: Boolean = true,
    env: mutable.Map[String, String] = mutable.Map.empty,
    tailChildContainers: Boolean = false,
    logConsumers: Seq[ServiceLogConsumer] = Seq.empty,
    waitingFor: Option[WaitingForService] = None,
    services: Services = Services.All,
    afterContainersStop: LazyList[Unit] = LazyList.empty,
) extends DockerComposeContainer(
      composeFiles,
      exposedServices,
      identifier,
      scaledServices,
      pull,
      localCompose,
      env.toMap,
      tailChildContainers,
      logConsumers,
      waitingFor,
      services,
    ) {

  override def stop(): Unit =
    try super.stop()
    finally afterContainersStop.foreach(identity)
}

object TestComposeContainer {

  case class Def(
      composeFiles: ComposeFile,
      exposedServices: Seq[ExposedService] = Seq.empty,
      identifier: String = DockerComposeContainer.randomIdentifier,
      scaledServices: Seq[ScaledService] = Seq.empty,
      pull: Boolean = true,
      localCompose: Boolean = true,
      env: mutable.Map[String, String] = mutable.Map.empty,
      tailChildContainers: Boolean = false,
      logConsumers: Seq[ServiceLogConsumer] = Seq.empty,
      waitingFor: Option[WaitingForService] = None,
      services: Services = Services.All,
      afterContainersStop: LazyList[Unit] = LazyList.empty,
  ) extends ContainerDef {

    override type Container = TestComposeContainer

    override def createContainer(): TestComposeContainer =
      TestComposeContainer(
        composeFiles,
        exposedServices,
        identifier,
        scaledServices,
        pull,
        localCompose,
        env,
        tailChildContainers,
        logConsumers,
        waitingFor,
        services,
        afterContainersStop,
      )
  }
}
