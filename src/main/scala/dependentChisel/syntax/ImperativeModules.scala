package dependentChisel.syntax

import scala.collection.mutable.ArrayBuffer
import dependentChisel.syntax.naming.Counter
import dependentChisel.syntax.tree.TopLevelCircuit
import dependentChisel.chiselDataTypes.*

/** imperative style for chisel ,record info in mutable vars inside class */
object ImperativeModules {
  case class DependenciesInfo(
      names: ArrayBuffer[String] = ArrayBuffer(),
      modules: ArrayBuffer[Module] = ArrayBuffer(),
      counter: Counter = new Counter()
  )
  case class ModLocalInfo(
      classNm: String,
      io: ArrayBuffer[String] = ArrayBuffer(),
      commands: ArrayBuffer[String] = ArrayBuffer()
  )

  trait Module { // extends Dependencies {
    val thisClassName = this.getClass.getCanonicalName.split('.').last.mkString
    given modLocalInfo: ModLocalInfo = ModLocalInfo(classNm = thisClassName)
    // def name = this.getClass.getCanonicalName.split('.').last
  }

  /* function style like when {} */
  type Ctrl = String
  trait UserModule(using parent: DependenciesInfo)
      extends Module,
        UserModuleOps {

    def create: Unit

    def pushF(ctr: Ctrl, uid: String, isStart: Boolean) = {
      modLocalInfo.commands.append(s"$ctr$uid $isStart")
    }

    def pushBlk(ctr: Ctrl)(block: => Any) = {
      val uid = parent.counter.getIdWithDash
      pushF(ctr, uid, true)
      block
      pushF(ctr, uid, false)
    }

    add2parent(parent, this)

  }

  trait UserModuleOps { ut: UserModule =>
    def If[w <: Int](b: Bool[w])(block: => Any) = pushBlk("if")(block)

    def IfElse[w <: Int](b: Bool[w])(block: => Any)(block2: => Any) = {
      If(b)(block)
      pushBlk("else")(block2)
    }
  }

  /* utils */
  private def add2parent(parent: DependenciesInfo, u: UserModuleOld) = {
    parent.names prepend u.thisClassName
    parent.modules prepend u
  }

  private def add2parent(parent: DependenciesInfo, u: UserModule) = {
    parent.names prepend u.thisClassName
    parent.modules prepend u
  }

  def makeModule[M <: Module](f: DependenciesInfo => M) = {
    val di = DependenciesInfo()
    val r = f(di)
    (r, di)
  }

  /* scaloid style like new When{} */
  trait UserModuleOld(using parent: DependenciesInfo) extends Module {

    def create: TopLevelCircuit
    add2parent(parent, this)
  }

}
