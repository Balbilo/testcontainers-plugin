package io.testcompose

import com.dimafeng.testcontainers.ContainerDef
import com.dimafeng.testcontainers.lifecycle.Stoppable
import org.testcontainers.lifecycle.*

/** A noop/dummy container as a workaround for the testcontainers library to satisfy the overridden data type Containers
  * in TestcontainersForAll.
  */
final private[testcompose] class RemoteTest extends Startable, Stoppable {
  override def start(): Unit = ()
  override def stop(): Unit  = ()
}

object RemoteTest {
  lazy val remoteTestContainer = new RemoteTest

  lazy val remoteTestContainerDef = new ContainerDef {
    override type Container = RemoteTest

    override def createContainer(): RemoteTest = remoteTestContainer
  }
}
