val scala3Version = "3.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dependentChisel",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq( 
      ("edu.berkeley.cs" %% "chisel3" % "3.5.5").cross(CrossVersion.for3Use2_13) // for 2.13 libs
    )
  )
