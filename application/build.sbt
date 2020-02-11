name := """application"""

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.6.1"
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-typed" % "2.6.1"
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-tools" % "2.6.1"
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

