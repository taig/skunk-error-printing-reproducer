ThisBuild / scalaVersion := "3.3.0"

libraryDependencies ++=
  "org.slf4j" % "slf4j-nop" % "1.7.36" ::
    "org.testcontainers" % "postgresql" % "1.18.3" ::
    "org.tpolecat" %% "skunk-core" % "0.6.0" ::
    Nil
