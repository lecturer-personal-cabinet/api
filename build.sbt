name := "api"
version := "0.1"
scalaVersion := "2.13.1"

val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-language:higherKinds",
  )
)

val slickPGExtensionsVersion = "0.18.1"
val macwireWiringVersion = "2.3.3"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.9",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "org.typelevel" %% "cats-core" % "2.0.0",
  "org.typelevel" %% "cats-effect" % "2.0.0",

  "com.github.tminglei" %% "slick-pg" % slickPGExtensionsVersion,
  "com.github.tminglei" %% "slick-pg_play-json" % slickPGExtensionsVersion,

  "com.softwaremill.macwire" %% "macros" % macwireWiringVersion % "provided",
  "com.softwaremill.macwire" %% "macrosakka" % macwireWiringVersion % "provided",
  "com.softwaremill.macwire" %% "util" % macwireWiringVersion,
  "com.softwaremill.macwire" %% "proxy" % macwireWiringVersion
)

lazy val api = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
