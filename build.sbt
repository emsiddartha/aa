version := "0.1"

scalaVersion := "2.13.0"

val scalactic = "org.scalactic" %% "scalactic" % "3.0.8"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
val scalaMock = "org.scalamock" %% "scalamock" % "4.4.0"
val akka = "com.typesafe.akka" %% "akka-actor" % "2.5.24"
val springBoot = "org.springframework.boot" % "spring-boot-starter-web" % "2.1.7.RELEASE"
val logging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
val json4s = "org.json4s" %% "json4s-jackson" % "3.6.7"
val mongodb = "org.mongodb.scala" %% "mongo-scala-driver" % "2.7.0"
val typesafe = "com.github.pureconfig" %% "pureconfig" % "0.11.1"

lazy val core = (project in file("core")).dependsOn(protocol).settings(
  name := "core",
  libraryDependencies += scalaTest % Test,
  libraryDependencies += scalaMock % Test,
  libraryDependencies += scalactic,
  libraryDependencies += akka,
  libraryDependencies += springBoot,
  libraryDependencies += logging,
  libraryDependencies += json4s,
  libraryDependencies += mongodb,
  libraryDependencies += typesafe
)

lazy val protocol = (project in file("protocol")).settings(
  name := "protocol"
)

lazy val root = (project in file(".")).aggregate(core,protocol).settings(
  name := "aa"
)