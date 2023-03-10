package dependentChisel.syntax

import scala.collection.mutable.ArrayBuffer
import dependentChisel.syntax.naming.Counter
import dependentChisel.syntax.tree.TopLevelCircuit

import dependentChisel.typesAndSyntax.all.*
import dependentChisel.codegen.firAST.*
import dependentChisel.codegen.seqCmds.*
import dependentChisel.typesAndSyntax.control.*

/** imperative style for chisel ,record info in mutable vars inside class */
object ImperativeModules {
  case class globalInfo(
      names: ArrayBuffer[String] = ArrayBuffer(),
      modules: ArrayBuffer[UserModule] = ArrayBuffer(),
      counter: Counter = new Counter()
  )
  case class ModLocalInfo(
      classNm: String,
      io: ArrayBuffer[String] = ArrayBuffer(),
      commands: ArrayBuffer[Cmds] = ArrayBuffer()
  )

  trait Module { // extends Dependencies {
    val thisClassName = this.getClass.getCanonicalName.split('.').last.mkString
    given modLocalInfo: ModLocalInfo = ModLocalInfo(classNm = thisClassName)
    // def name = this.getClass.getCanonicalName.split('.').last
  }

  /* function style like when {} */
  type Ctrl = String
  trait UserModule(using parent: globalInfo) extends Module, UserModuleOps {

    // def create: Unit

    def pushF(ctr: Ctrl, uid: String, isStart: Boolean) = {
      // modLocalInfo.commands.append(s"$ctr$uid $isStart")
      modLocalInfo.commands.append(Block(s"$ctr$uid $isStart"))
    }

    def pushBlk(ctr: Ctrl)(block: => Any) = {
      val uid = parent.counter.getIdWithDash
      pushF(ctr, uid, true)
      block
      pushF(ctr, uid, false)
    }

    add2parent(parent, this)

  }

  /* utils */

  private def add2parent(parent: globalInfo, u: UserModule) = {
    parent.names prepend u.thisClassName
    parent.modules prepend u
  }

  def makeModule[M <: Module](f: globalInfo => M) = {
    val di = globalInfo()
    val r = f(di)
    (r, di)
  }

  /* scaloid style like new When{} */

}
