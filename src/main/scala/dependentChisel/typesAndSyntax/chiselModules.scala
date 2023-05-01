package dependentChisel.typesAndSyntax

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

import dependentChisel.codegen.firrtlTypes.*
import dependentChisel.codegen.seqCommands.*
import dependentChisel.codegen.seqCommands

import scala.reflect.ClassTag
import dependentChisel.global.getUid
import dependentChisel.syntax.naming

import dependentChisel.typesAndSyntax.control
import dependentChisel.typesAndSyntax.varDecls.UserModuleDecls
import dependentChisel.global
import scala.util.Try
import scala.util.Failure
import scala.util.Success

/** imperative style for chisel ,record info in mutable vars inside class chiselModules
  */
object chiselModules {
  case class GlobalInfo(
      names: ArrayBuffer[String] = ArrayBuffer(),
      modules: ArrayBuffer[UserModule] = ArrayBuffer()
  )
  case class ModLocalInfo(
      className: String,
      thisInstanceName: String,
      io: ArrayBuffer[IOdef] = ArrayBuffer(),
      commands: ArrayBuffer[Cmds] = ArrayBuffer(), // list of seq cmds
      typeMap: mutable.Map[Expr[?] | Var[?], Option[Int]] =
        mutable.Map() // list of seq cmds
  )

  // trait Module {}

  /* function style UserModule ,for example: when {} else {} */
  trait UserModule(using parent: GlobalInfo) extends UserModuleOps, UserModuleDecls {
    val thisClassName =
      (Try(
        this.getClass.getCanonicalName.split('.').last.mkString
      ) match
        case Failure(exception) => "noName"
        case Success(value)     => value
      ) + naming.getIdWithDash

    /** Name for this Instance after new class.. */
    val thisInstanceName = naming.mkUidFrom(thisClassName)
    if (global.debugVerbose) println(s"new inst $thisInstanceName for $thisClassName")

    given modLocalInfo: ModLocalInfo =
      ModLocalInfo(className = thisClassName, thisInstanceName = thisInstanceName)
    // def name = this.getClass.getCanonicalName.split('.').last
    val globalInfo = parent

    def pushCmd(cmd: Cmds) = {
      modLocalInfo.commands.append(cmd)
    }

    def pushBlk(ctr: Ctrl)(block: => Any) = {
      val uid = naming.getIntId
      pushCmd(seqCommands.Start(ctr, uid))
      block
      pushCmd(seqCommands.End(ctr, uid))
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
