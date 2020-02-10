name := "api"
version := "0.1"
scalaVersion := "2.13.1"

resolvers += Resolver.jcenterRepo

val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-language:higherKinds",
  )
)

val slickPGExtensionsVersion = "0.18.1"
val macwireWiringVersion = "2.3.3"
val reactiveMongoVer = "0.19.3-play27"
val silhouetteVer = "6.1.0"

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
  "com.softwaremill.macwire" %% "proxy" % macwireWiringVersion,

  "com.mohiva" %% "play-silhouette" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-persistence" % silhouetteVer,
)

libraryDependencies += filters

lazy val api = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
