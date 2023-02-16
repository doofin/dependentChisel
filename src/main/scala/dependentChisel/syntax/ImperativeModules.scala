package dependentChisel.syntax

import scala.collection.mutable.ArrayBuffer

/** imperative style for chisel */
object ImperativeModules {
  case class DependenciesInfo(
      names: ArrayBuffer[String] = ArrayBuffer(),
      modules: ArrayBuffer[UserModule] = ArrayBuffer()
  )
  case class ModCircuits(
      classNm: String,
      io: ArrayBuffer[String] = ArrayBuffer(),
      commands: ArrayBuffer[String] = ArrayBuffer()
  ) {
    /* override def toString(): String = {
      io.mkString("/n") ++
        commands.mkString("/n")
    } */
  }

  trait Module { // extends Dependencies {

    def name = this.getClass.getCanonicalName.split('.').last
  }

  class UserModule(using parent: DependenciesInfo) extends Module {
    val thisClassName = this.getClass.getCanonicalName
    parent.names prepend thisClassName
    parent.modules.prepend(this)
    given modCircuits: ModCircuits = ModCircuits(classNm = thisClassName) // .split('.').last.mkString
    // implicit val clasNm: String = this.getClass.getCanonicalName
    // given modInfo: ModInfo = ModInfo(thisClassName)
  }

  def makeModule(f: DependenciesInfo => UserModule) = {
    val di = DependenciesInfo()
    f(di)
    di
  }
}
