package io.testcompose

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper

import java.io.File
import scala.util.chaining.scalaUtilChainingOps

private[testcompose] object TestComposeUtils {
  private lazy val servicesFieldName = "services"
  private lazy val portsFieldName    = "ports"
  private lazy val tempFilePrefix    = ".testcontainers"

  /** Remove ports section from compose.yaml files. Since testcontainers supports dynamic port assignment for containers
    * using socat executing test using the library should not rely on ports defined in docker compose file
    *
    * @example
    *   {{{
    *   # Input docker compose file
    *   services:
    *   test-containers-service:
    *     image: ${DOCKER_REPOSITORY}/example-container:latest
    *     ports: <- This section will be removed
    *       - "8080:8080"
    *     restart: always
    *
    *   # Output docker-compose file
    *   services:
    *   test-containers-service:
    *     image: ${DOCKER_REPOSITORY}/example-container:latest
    *     restart: always
    *   }}}
    */
  def removePortsAndWriteTemp(composeFile: File, testName: String): File = {
    val mapper              = new YAMLMapper()
    val node                = mapper.readTree(composeFile)
    val composeTempFileName = s"$tempFilePrefix-temp-$testName.yaml"

    node.get(servicesFieldName).properties().forEach { service =>
      val serviceNode = service.getValue.asInstanceOf[ObjectNode]
      val _           = serviceNode.remove(portsFieldName)
    }

    val parentDir = composeFile.getParentFile
    val tempFile  = new File(parentDir, composeTempFileName)

    tempFile.tap(mapper.writeValue(_, node))
  }

  /** Removes temporary compose file created with ports removed
    */
  def deleteTemp(composeTempFile: File): Unit = {
    val _ = composeTempFile.delete()
  }
}

//private[testcompose] object DockerComposeFileUtils {
//  private lazy val servicesFieldName = "services"
//  private lazy val portsFieldName    = "ports"
//
//  /** Remove ports section from compose.yaml files. Since testcontainers supports dynamic port assignment for containers
//    * executing test using the library should not rely on ports defined in docker-compose file
//    *
//    * @example
//    *   {{{
//    *   # Input docker-compose file
//    *   services:
//    *   test-containers-service:
//    *     image: ${DOCKER_REPOSITORY}/examples-testcontainers-core:latest
//    *     ports: <- This section will be removed
//    *       - "8080:8080"
//    *     restart: always
//    *     environment:
//    *       LOG_ENVIRONMENT: "docker-compose"
//    *       LOG_SERVICE_NAME: "opt-out-proxy"
//    *       LOG_TOP_TENANT: "top"
//    *       LOG_SERVICE_ID: "SCV123JDN"
//    *
//    *   # Output docker-compose file
//    *   services:
//    *   test-containers-service:
//    *     image: ${DOCKER_REPOSITORY}/examples-testcontainers-core:latest
//    *     restart: always
//    *     environment:
//    *       LOG_ENVIRONMENT: "docker-compose"
//    *       LOG_SERVICE_NAME: "opt-out-proxy"
//    *       LOG_TOP_TENANT: "top"
//    *       LOG_SERVICE_ID: "SCV123JDN"
//    *   }}}
//    */
//  def removePortsAndWriteTemp(composeFile: File, testName: String): File = {
//    val mapper              = new YAMLMapper()
//    val node                = mapper.readTree(composeFile)
//    val tempFilePrefix      = ".testcontainers"
//    val composeTempFileName = s"$tempFilePrefix-temp-$testName.yaml"
//
//    node.get(servicesFieldName).fields().forEachRemaining { service =>
//      val serviceNode = service.getValue.asInstanceOf[ObjectNode]
//      val _           = serviceNode.remove(portsFieldName)
//    }
//
//    val parentDir = composeFile.getParentFile
//    val tempFile  = new File(parentDir, composeTempFileName)
//
//    tempFile.tap(mapper.writeValue(_, node))
//  }
//
//  /** Removes temporary compose file created with ports removed
//    */
//  def deleteTemp(composeTempFile: File): Unit = {
//    val _ = composeTempFile.delete()
//  }
//}
