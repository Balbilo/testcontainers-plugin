package io.testcompose

final case class DockerComposeService(
  name: String,
  image: String,
  ports: List[String],
  environment: Map[String, String],
  volumes: List[String],
  networks: List[String],
  dependsOn: List[String] = List.empty
) {
  def withDependsOn(dependencies: String*): DockerComposeService =
    copy(dependsOn = dependsOn ++ dependencies)
}
