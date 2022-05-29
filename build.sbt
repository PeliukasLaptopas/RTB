organization := "com.lightbend.akka.samples"
name := "akka-sample-replicated-event-sourcing-scala"

scalaVersion := "2.13.5"

lazy val akkaHttpVersion = "10.2.9"
lazy val akkaVersion    = "2.6.19"

libraryDependencies ++= Seq(

  "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
  "ch.qos.logback"    % "logback-classic"           % "1.2.3",

  "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
  "org.scalatest"     %% "scalatest"                % "3.1.4"         % Test,

  "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-testkit" % akkaVersion % Test,

  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,

  "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,

  "com.lightbend.akka" %% "akka-projection-core" % "1.2.4",

  "org.typelevel" %% "cats-core" % "2.1.1",
  "org.typelevel" %% "cats-mtl" % "1.1.1",
)
