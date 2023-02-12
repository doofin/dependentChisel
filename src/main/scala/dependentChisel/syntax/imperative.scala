package dependentChisel.syntax

import scala.collection.mutable.ArrayBuffer

/** imperative style for chisel */
object imperative {
  case class DependenciesInfo(
      names: ArrayBuffer[String] = ArrayBuffer(),
      mods: ArrayBuffer[Module] = ArrayBuffer()
  )
  case class ModCircuits(
      commands: ArrayBuffer[String] = ArrayBuffer(),
      io: ArrayBuffer[String] = ArrayBuffer()
  ) {
    override def toString(): String = {
      io.mkString("/n") ++
        commands.mkString("/n")
    }
  }

  /** */
  trait Dependencies {
    // given DependenciesInfo = DependenciesInfo()
  }

  trait Module extends Dependencies {
    given modCircuits: ModCircuits = ModCircuits()
    def name = this.getClass.getCanonicalName.split('.').last
  }

  class UserModule(using parent: DependenciesInfo) extends Module {
    parent.names prepend this.getClass.getCanonicalName()
    parent.mods.prepend(this)
  }

  def instModule(f: DependenciesInfo => UserModule) = {
    val di = DependenciesInfo()
    f(di)
    di
  }
}
