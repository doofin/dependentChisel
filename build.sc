import mill._, scalalib._

object dep extends ScalaModule {
  override def scalaVersion = "3.2.1"
  override def ivyDeps = Agg(
    ivy"edu.berkeley.cs::chisel3:3.5.5".withDottyCompat(scalaVersion())
  )

  /* def ivyDeps = Agg(
  ivy"com.lihaoyi::upickle:2.0.0".withDottyCompat(scalaVersion()) //1
) */
}
