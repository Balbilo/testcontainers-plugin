package io.testcompose

import cats.effect.kernel.Resource
import com.dimafeng.testcontainers.scalatest.TestContainersForAll
import org.scalatest.Suite
import zio.*

trait ZTestComposeBase[Context] extends TestComposeBase[Task, Context], TestContainersForAll {
  suite: Suite =>

  def bootstrap: ULayer[Unit]                = ZLayer.unit
  def configProvider[B <: ConfigProvider]: B = ConfigProvider.defaultProvider

  /** This method facilitates passing a context to each test, users are required to implement a context-building
    * mechanism using containers from testcontainers.
    *
    * @return
    *   Resource[F, Context]
    */
  def contextBuilder(containers: Containers): RIO[Any, Context]

  final override def withContext[A](block: Context => Task[A]): Task[A] =
    withContainers(contextBuilder).flatMap(block)

  final override def withUnsafeContext[A](block: Context => A): A =
    withContainers { containers =>
      Unsafe.unsafe(implicit unsafe =>
        Runtime.default.unsafe
          .run[Throwable, A](
            contextBuilder(containers)
              .map(block)
              .provideLayer(bootstrap)
              .withConfigProvider(configProvider)
          )
          .getOrThrowFiberFailure()
      )
    }
}
