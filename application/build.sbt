name := """application"""

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += guice
libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.6.1"
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-tools" % "2.6.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test
libraryDependencies += "com.jayway.jsonpath" % "json-path-assert" % "2.4.0" % Test
