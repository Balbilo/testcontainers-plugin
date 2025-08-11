package io.testcompose

import com.dimafeng.testcontainers.ContainerDef
import com.dimafeng.testcontainers.lifecycle.Stoppable
import org.testcontainers.lifecycle.*

final private[testcompose] class NoopContainer extends Startable, Stoppable {
  override def start(): Unit = ()
  override def stop(): Unit  = ()
}

object NoopContainer {
  lazy val noopContainer = new NoopContainer

  lazy val noopContaineDef = new ContainerDef {
    override type Container = NoopContainer

    override def createContainer(): NoopContainer = noopContainer
  }
}
