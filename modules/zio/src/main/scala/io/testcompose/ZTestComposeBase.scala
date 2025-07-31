package io.testcompose

import cats.effect.kernel.Resource
import zio.*

trait ZTestComposeBase[Config, Context] extends TestComposeBase[Task, Config, Context] {

  def bootstrap: ULayer[Unit]                = ZLayer.unit
  def configProvider[B <: ConfigProvider]: B = ConfigProvider.defaultProvider

  def withUnsafeContext[A](block: Context => A): A =
    Unsafe.unsafe(implicit unsafe =>
      Runtime.default.unsafe
        .run[Nothing, Either[E, A]](
          withContext(block(_))
            .provideLayer(bootstrap)
            .withConfigProvider(configProvider)
        )
        .getOrThrowFiberFailure()
    )
}
