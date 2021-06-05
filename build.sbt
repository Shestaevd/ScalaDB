
lazy val scalaDB = (project in file("scalaDB"))
  .settings(globalSettings)
  .settings(scalaTest)
  .settings(libraryDependencies += dependencies.catsEffect)
  .settings(libraryDependencies ++= dependencies.scalaTest)
  .settings(libraryDependencies ++= dependencies.akkaStreams)
  .dependsOn(commons, serializations)

lazy val commons = (project in file("commons"))
  .settings(globalSettings)

lazy val serializations = (project in file("serialization"))
  .settings(globalSettings)
  .settings(scalaTest)
  .settings(libraryDependencies ++= dependencies.scalaTest)
  .dependsOn(commons)

lazy val globalSettings = Seq(
  ThisBuild / name := "ScalaTextDB",
  ThisBuild / organization := "com.scalaDB",
  ThisBuild / scalaVersion := "2.13.6",
  version := "0.1",
)

lazy val scalaTest = Seq(
  Test / logBuffered := false
)

lazy val dependencies = new {
  private val scalaTestV = "3.2.7"
  private val AkkaVersion = "2.6.14"

  private val catsEffectV = "3.1.1"

  private val sf2V = "3.0.4"

  lazy val scalaTest = Seq(
    "org.scalactic" %% "scalactic" % scalaTestV % "test",
    "org.scalatest" %% "scalatest" % scalaTestV % "test"
  )

  lazy val fs2Core = "co.fs2" %% "fs2-core" % sf2V

  // optional I/O library
  lazy val fs2IO = "co.fs2" %% "fs2-io" % sf2V

  lazy val akkaStreams= Seq(
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
  )

  // https://mvnrepository.com/artifact/org.typelevel/cats-effect
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectV



}