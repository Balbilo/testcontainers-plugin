addSbtPlugin("org.scalameta" % "sbt-scalafmt"             % "2.5.4")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix"             % "0.14.2")
addSbtPlugin("org.scoverage" % "sbt-scoverage"            % "2.3.1")
addSbtPlugin("org.typelevel" % "sbt-tpolecat"             % "0.5.2")
addSbtPlugin("com.eed3si9n"  % "sbt-buildinfo"            % "0.13.1")
addSbtPlugin("org.typelevel" % "sbt-typelevel-ci-release" % "0.8.0")

//Compile / unmanagedSourceDirectories +=
//  (ThisBuild / baseDirectory).value.getParentFile / "modules" / "sbt" / "src" / "main" / "scala"
