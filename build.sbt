val scala3Version = "3.2.1"

resolvers ++= Seq(
  Resolver.bintrayRepo("veinhorn", "maven"),
  Resolver.jcenterRepo,
  "jitpack" at "https://jitpack.io",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "dependentChisel",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "pprint" % "0.8.1", // print,debug
      "io.bullet" %% "macrolizer" % "0.6.2" % "compile-internal", // print,debug
      "org.typelevel" %% "cats-core" % "2.9.0",
      ("edu.berkeley.cs" %% "chisel3" % "3.5.0-RC1").cross(CrossVersion.for3Use2_13) // for 2.13 libs
      // ("edu.berkeley.cs" %% "chisel3" % "3.5.5").cross(CrossVersion.for3Use2_13) // for 2.13 libs
    )
  )
