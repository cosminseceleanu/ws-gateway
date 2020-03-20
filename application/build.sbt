name := """application"""
version := "1.0-SNAPSHOT"
lazy val root = (project in file(".")).enablePlugins(PlayScala)

val akkaVersion = "2.5.27"
val akkaClusterVersion = "2.6.4"
val akkaHTTPVersion = "10.1.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"  % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"  % akkaVersion,

  "com.typesafe.akka" %% "akka-http" % akkaHTTPVersion,
  "com.typesafe.akka" %% "akka-http-core" % akkaHTTPVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHTTPVersion,
  "com.typesafe.akka" %% "akka-parsing" % akkaHTTPVersion,

  "com.typesafe.akka" %% "akka-cluster" % akkaClusterVersion,
  "com.typesafe.akka" %% "akka-cluster-typed" % akkaClusterVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaClusterVersion,
  "com.lightbend.akka.management" %% "akka-management" % "1.0.6"
)

libraryDependencies += guice
libraryDependencies += ws

libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.6"
libraryDependencies += "com.jayway.jsonpath" % "json-path" % "2.4.0"

//validation dependencies
libraryDependencies += "javax.el" % "javax.el-api" % "3.0.1-b06"
libraryDependencies += "org.glassfish" % "javax.el" % "3.0.1-b06"
libraryDependencies += "javax.validation" % "validation-api" % "2.0.1.Final"
libraryDependencies += "org.hibernate.validator" % "hibernate-validator" % "6.1.2.Final"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test
libraryDependencies += "com.jayway.jsonpath" % "json-path-assert" % "2.4.0" % Test
libraryDependencies += "com.github.tomakehurst" % "wiremock" % "2.25.1" % Test

val jettyVersion = "9.2.28.v20190418"

dependencyOverrides += "org.eclipse.jetty" % "jetty-server" % jettyVersion
dependencyOverrides += "org.eclipse.jetty" % "jetty-servlet" % jettyVersion
dependencyOverrides += "org.eclipse.jetty" % "jetty-security" % jettyVersion
dependencyOverrides += "org.eclipse.jetty" % "jetty-servlets" % jettyVersion
dependencyOverrides += "org.eclipse.jetty" % "jetty-continuation" % jettyVersion
dependencyOverrides += "org.eclipse.jetty" % "jetty-webapp" % jettyVersion
dependencyOverrides += "org.eclipse.jetty" % "jetty-xml" % jettyVersion
dependencyOverrides += "org.eclipse.jetty" % "jetty-client" % jettyVersion
dependencyOverrides += "org.eclipse.jetty" % "jetty-http" % jettyVersion
dependencyOverrides += "org.eclipse.jetty" % "jetty-io" % jettyVersion
dependencyOverrides += "org.eclipse.jetty" % "jetty-util" % jettyVersion
dependencyOverrides += "org.eclipse.jetty.websocket" % "websocket-api" % jettyVersion
dependencyOverrides += "org.eclipse.jetty.websocket" % "websocket-common" % jettyVersion
dependencyOverrides += "org.eclipse.jetty.websocket" % "websocket-client" % jettyVersion
