package io.testcompose.service.config

import com.comcast.ip4s.*
import io.poc.twilio.config.AppConfig.ServerConfig
import zio.config.*
import zio.config.magnolia.*

final case class AppConfig(service: ServerConfig, health: ServerConfig)

object AppConfig {
  final case class ServerConfig(host: Host, port: Port)

  val live = deriveConfigLayer[AppConfig]("server")
}
