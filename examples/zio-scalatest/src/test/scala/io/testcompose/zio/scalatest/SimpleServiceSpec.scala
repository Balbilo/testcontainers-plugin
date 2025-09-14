package io.testcompose.zio.scalatest

import io.testcompose.ZTestComposeBase
import io.testcompose.zio.SimpleServiceSpec.*

class SimpleServiceSpec extends ZTestComposeBase[SimpleServiceConfig, SimpleServiceContext] {

  "SimpleService" should {
    "provide a simple service" in withUnsafeContext { context =>
      assert(context.simpleService.isDefined)(isTrue)
    }
  }
}

object SimpleServiceSpec {
  final case class SimpleServiceContext()
  final case class SimpleServiceConfig()
}
