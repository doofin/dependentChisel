package dependentChisel.typesAndSyntax

import scala.collection.mutable.ArrayBuffer

import dependentChisel.syntax.naming.Counter

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

import dependentChisel.codegen.firrtlTypes.*
import dependentChisel.codegen.seqCmdTypes.*
import dependentChisel.codegen.seqCmdTypes

import scala.reflect.ClassTag
import dependentChisel.global.getUid

/** imperative style for chisel ,record info in mutable vars inside class
  * chiselModules
  */
object chiselModules {
  case class GlobalInfo(
      names: ArrayBuffer[String] = ArrayBuffer(),
      modules: ArrayBuffer[UserModule] = ArrayBuffer(),
      counter: Counter = new Counter()
  )
  case class ModLocalInfo(
      classNm: String,
      instNm: String,
      io: ArrayBuffer[IOdef] = ArrayBuffer(),
      commands: ArrayBuffer[Cmds] = ArrayBuffer() // list of seq cmds
  )

  // trait Module {}

  /* function style UserModule ,for example: when {} else {} */
  trait UserModule(using parent: GlobalInfo) extends UserModuleOps {
    val thisClassName = this.getClass.getCanonicalName.split('.').last.mkString
    val thisInstanceName = ""

    given modLocalInfo: ModLocalInfo =
      ModLocalInfo(classNm = thisClassName, instNm = thisClassName + getUid)
    // def name = this.getClass.getCanonicalName.split('.').last
    val globalInfo = parent

    def pushCmd(cmd: Cmds) = {
      modLocalInfo.commands.append(cmd)
    }

    def pushBlk(ctr: Ctrl)(block: => Any) = {
      val uid = parent.counter.getIntId
      pushCmd(seqCmdTypes.Start(ctr, uid))
      block
      pushCmd(seqCmdTypes.End(ctr, uid))
    }

    add2parent(parent, this)
  }

  /* utils */

  private def add2parent(parent: GlobalInfo, u: UserModule) = {
    parent.names prepend u.thisClassName
    parent.modules prepend u
  }

  def makeModule[M <: UserModule](f: GlobalInfo => M) = {
    val di = GlobalInfo()
    val r = f(di)
    (r, di)
  }

}
