package dependentChisel.syntax

import scala.collection.mutable.ArrayBuffer

import dependentChisel.syntax.naming.Counter
import dependentChisel.syntax.tree.TopLevelCircuit

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

import dependentChisel.codegen.firrtlTypes.*
import dependentChisel.codegen.seqCmds.*
import dependentChisel.codegen.seqCmds

/** imperative style for chisel ,record info in mutable vars inside class
  * chiselModules
  */
object imperativeModules {
  case class globalInfo(
      names: ArrayBuffer[String] = ArrayBuffer(),
      modules: ArrayBuffer[UserModule] = ArrayBuffer(),
      counter: Counter = new Counter()
  )
  case class ModLocalInfo(
      classNm: String,
      io: ArrayBuffer[IOdef] = ArrayBuffer(),
      commands: ArrayBuffer[Cmds] = ArrayBuffer() // list of seq cmds
  )

  trait Module { // extends Dependencies {
    val thisClassName = this.getClass.getCanonicalName.split('.').last.mkString
    given modLocalInfo: ModLocalInfo = ModLocalInfo(classNm = thisClassName)
    // def name = this.getClass.getCanonicalName.split('.').last
  }

  /* function style like when {} */
  // type Ctrl = String
  trait UserModule(using parent: globalInfo) extends Module, UserModuleOps {

    // def create: Unit

    def pushF(cmd: Cmds) = {
      // modLocalInfo.commands.append(s"$ctr$uid $isStart")
      // modLocalInfo.commands.append(Block(s"$ctr$uid $isStart"))
      modLocalInfo.commands.append(cmd)
    }

    def pushBlk(ctr: Ctrl)(block: => Any) = {
      val uid = parent.counter.getIntId
      pushF(seqCmds.Start(ctr, uid))
      block
      pushF(seqCmds.End(ctr, uid))
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

  import scala.reflect.ClassTag
  def makeModule2[M <: Module: ClassTag](f: globalInfo => M) = {
    val di = globalInfo()
    // new M.getClass
    // new M()
    val r = f(di)
    (r, di)
  }
  /* scaloid style like new When{} */

}
