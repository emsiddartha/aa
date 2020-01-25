ThisBuild / version := "0.1"

ThisBuild / organization := "org.bheaver.ngl4"
ThisBuild / scalaVersion := "2.13.0"


val scalactic = "org.scalactic" %% "scalactic" % "3.0.8"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
val scalaMock = "org.scalamock" %% "scalamock" % "4.4.0"
val springBoot = "org.springframework.boot" % "spring-boot-starter-web" % "2.1.7.RELEASE"
val logging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
val json4s = "org.json4s" %% "json4s-jackson" % "3.6.7"
val mongodb = "org.mongodb.scala" %% "mongo-scala-driver" % "2.7.0"
val typesafe = "com.github.pureconfig" %% "pureconfig" % "0.11.1"
val jwtJson4s = "com.pauldijou" %% "jwt-json4s-jackson" % "4.0.0"


val utillib = "org.bheaver.ngl4" %% "util-lib" % "0.1"
val scalatime = "com.github.nscala-time" %% "nscala-time" % "2.22.0"

val asyncHttpClient = "com.softwaremill.sttp" %% "async-http-client-backend-future" % "1.6.5"

val scalacompat =  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0"

val guice = "com.google.inject" % "guice" % "4.2.2"

lazy val aacore = (project in file("core")).dependsOn(protocol).settings(
  name := "aa-core",
  libraryDependencies += scalaTest % Test,
  libraryDependencies += scalaMock % Test,
  libraryDependencies += scalactic,
  libraryDependencies += springBoot,
  libraryDependencies += logging,
  libraryDependencies += json4s,
  libraryDependencies += mongodb,
  libraryDependencies += typesafe,
  libraryDependencies += jwtJson4s,
  libraryDependencies += utillib,
  libraryDependencies += scalatime,
  libraryDependencies += scalacompat,
  libraryDependencies += guice
)

lazy val protocol = (project in file("protocol")).settings(
  name := "aa-protocol",
  libraryDependencies += asyncHttpClient,
  libraryDependencies += utillib,
  libraryDependencies += logging,
  libraryDependencies += json4s,
  libraryDependencies += jwtJson4s,
  libraryDependencies += guice
)

lazy val root = (project in file(".")).aggregate(aacore,protocol).settings(
  name := "aa"
)
