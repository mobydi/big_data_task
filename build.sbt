name := "scalaProj"

version := "1.0"

scalaVersion := "2.11.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "twitter4j" at "http://twitter4j.org/maven2",
  "clojars.org" at "http://clojars.org/repo"
)

lazy val cassandraDependencies = Seq (
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.2"
)

libraryDependencies ++= {
  val sprayVersion = "1.3.1"
  val akkaVersion = "2.4.0"
  Seq(
    "org.apache.storm" % "storm-kafka" % "0.10.0",
    "org.apache.storm" % "storm-core" % "0.9.3" % "compile" exclude("junit", "junit"),
    "org.apache.kafka" %% "kafka" % "0.8.2.2"
      exclude("javax.jms", "jms")
      exclude("com.sun.jdmk", "jmxtools")
      exclude("com.sun.jmx", "jmxri")
      excludeAll ExclusionRule("org.slf4j"),
    "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.2",
    "com.typesafe" % "config" % "1.3.0",
    "com.typesafe.play" %% "play-json" % "2.4.3",
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-testkit" % sprayVersion
      exclude("com.typesafe.akka", "akka-actor_2.10")
      exclude("com.typesafe.akka", "akka-testkit_2.10"),
    "io.spray" %% "spray-client" % sprayVersion,
    "io.spray" %%  "spray-json" % "1.3.2",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion  exclude("com.typesafe.akka", "akka-actor_2.10"),
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test" exclude("com.typesafe.akka", "akka-testkit_2.10"),
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  )
}
