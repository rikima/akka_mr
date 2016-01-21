name := "akka_mr"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Akka Repository" at "http://repo.akka.io/releases/",
  "Spray Repository" at "http://repo.spray.cc/"
)

libraryDependencies ++= Seq(
 "com.typesafe.akka" % "akka-cluster-metrics_2.11" % "2.4.1",
 "com.typesafe.akka" % "akka-actor_2.11" % "2.4.1",
 "com.typesafe.akka" % "akka-cluster_2.11" % "2.4.1",
 "com.typesafe.akka" % "akka-remote_2.11" % "2.4.1"
)

assemblySettings    
