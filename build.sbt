import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

lazy val formatSettings = scalariformSettings ++ Seq(
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(MultilineScaladocCommentsStartOnFirstLine, true)
//  .setPreference(ScaladocCommentsStopOnLastLine, true)
  .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
  .setPreference(SpacesAroundMultiImports, false)
)

lazy val commonSettings = formatSettings ++ Seq(
  version := "7.2.1",
  scalaVersion := "2.11.7",
//  crossScalaVersions := Seq(scalaVersion.value, "2.12.0-M3"), todo enable when play-json is built for 2.12
  organization := "com.sandinh",

  //see https://github.com/scala/scala/blob/2.11.x/src/compiler/scala/tools/nsc/settings/ScalaSettings.scala
  scalacOptions ++= Seq("-encoding", "UTF-8"
    ,"-deprecation", "-unchecked", "-feature"
    ,"-optimise"
    ,"-Xfuture" //, "–Xverify", "-Xcheck-null"
    ,"-Ybackend:GenBCode"
    ,"-Ydelambdafy:method"
    ,"-Yinline-warnings" //, "-Yinline"
    ,"-Ywarn-dead-code", "-Ydead-code"
    ,"-Yclosure-elim"
    ,"-Ywarn-unused-import", "-Ywarn-numeric-widen"
    //`sbt doc` will fail if enable the following options!
    //,"nullary-unit", "nullary-override", "unsound-match", "adapted-args", "infer-any"
  ),

  libraryDependencies ++= Seq(
    "org.specs2"  %% "specs2-junit" % "3.6.6" % Test
  ),

  dependencyOverrides ++= Set(
    //transitive dep from com.couchbase.client:core-io
    "io.reactivex"    % "rxjava"        % "1.1.1",
    "org.scala-lang"  % "scala-reflect" % scalaVersion.value // % Optional
  ),

  testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "junitxml", "console"),

  resolvers ++= Seq(
    Resolver.typesafeRepo("releases"),
    Resolver.bintrayRepo("scalaz", "releases")
  )
)

lazy val core = (project in file("core"))
  .settings(commonSettings ++ Seq(
    name := "couchbase-scala",
    libraryDependencies ++= Seq(
      "com.couchbase.client"      %  "java-client"        % "2.2.4",
      "io.reactivex"              %% "rxscala"            % "0.26.0",
      "javax.inject"              % "javax.inject"        % "1",
      "com.typesafe.play"         %% "play-json"          % "2.4.6", //require java 8
      "com.google.inject"         % "guice"               % "4.0"       % Test
    )
  ))

lazy val play = (project in file("play"))
  .settings(commonSettings ++ Seq(
    name := "couchbase-play",
    libraryDependencies ++= Seq(
      "com.sandinh" %% "play-alone" % "2.4.3" % Optional
    )
  )).dependsOn(core)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    packagedArtifacts := Map.empty
  ).aggregate(play, core)
