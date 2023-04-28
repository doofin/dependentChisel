resolvers ++= Seq(
  Resolver.bintrayRepo("veinhorn", "maven"),
  Resolver.jcenterRepo,
  "jitpack" at "https://jitpack.io",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)
val mScala3Version = "3.2.2" // "3.3.1-RC1-bin-SNAPSHOT" 3.3.0-RC3

/* To print the code as it is transformed through the compiler, use the compiler flag -Xprint:all
trace the code that generated the error by adding the -Ydebug-error compiler flag,
 */
val mScalacOptions = Seq("-source", "future") // "-Ydebug-error"
val catsV = "2.9.0"
lazy val root = project
  .in(file("."))
  .settings(
    name := "dependentChisel",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := mScala3Version,
    scalacOptions ++= mScalacOptions,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "pprint" % "0.8.1", // print,debug
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      // https://mvnrepository.com/artifact/org.scalatest/scalatest
      "org.scalatest" %% "scalatest" % "3.2.14" % Test,
      "io.bullet" %% "macrolizer" % "0.6.2" % "compile-internal", // print,debug
      "org.typelevel" %% "cats-core" % catsV,
      "org.typelevel" %% "cats-free" % catsV,
      "com.github.doofin.stdScala" %% "stdscala" % "b10536c37c", // new : 184b5cbc7d  %%% for cr
      // ("edu.berkeley.cs" %% "chisel3" % "3.5.0-RC1").cross(CrossVersion.for3Use2_13) // for 2.13 libs
      ("edu.berkeley.cs" %% "chisel3" % "3.5.5")
        .cross(CrossVersion.for3Use2_13) // for 2.13 libs
    )
  )
