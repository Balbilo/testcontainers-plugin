package io.testcompose.zio

import io.testcompose.ZTestComposeBase

class SimpleServiceSpec extends ZTestComposeBase[SimpleServiceConfig, SimpleServiceContext] {

  "SimpleService" should {
    "provide a simple service" in withUnsafeContext { context =>
      assert(context.simpleService.isDefined)(isTrue)
    }
  }
}

object SimpleServiceSpec {
  final case class 
}