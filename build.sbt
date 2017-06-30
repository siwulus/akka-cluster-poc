name := "akka-cluster-poc"

version := "1.0"

scalaVersion := "2.12.2"

val akkaVersion = "2.5.3"

resolvers += "JBoss" at "https://repository.jboss.org"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.0.9",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)