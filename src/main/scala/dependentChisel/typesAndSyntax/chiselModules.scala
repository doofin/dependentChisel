package dependentChisel.typesAndSyntax

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

import dependentChisel.codegen.firrtlTypes.*
import dependentChisel.codegen.sequentialCommands.*
import dependentChisel.codegen.sequentialCommands

import scala.reflect.ClassTag
import dependentChisel.global.getUid
import dependentChisel.syntax.naming

import dependentChisel.typesAndSyntax.control
import dependentChisel.typesAndSyntax.varDecls.UserModuleDecls
import dependentChisel.global
import scala.util.Try
import scala.util.Failure
import scala.util.Success

/** IR for current implementation
  *
  * imperative chisel like DSL, which records info in mutable vars inside class chiselModules during
  * construction
  */
object chiselModules {

  /** global info like list of all module
    *
    * @param names
    * @param modules
    */
  case class GlobalInfo(
      names: ArrayBuffer[String] = ArrayBuffer(),
      modules: ArrayBuffer[UserModule] = ArrayBuffer()
  )

  /** AST(Abstract Syntax Tree) for each module, contains all info about a module
    *
    * @param commands:
    *   list of statements, to represent the circuits
    */
  case class ModuleData(
      className: String,
      instanceName: String,
      io: ArrayBuffer[IOdef] = ArrayBuffer(),
      commands: ArrayBuffer[Cmds] = ArrayBuffer(), // list of statements
      typeMap: mutable.Map[Expr[?] | Var[?], Int] = mutable.Map() // list of seq cmds
  )

  /* function style UserModule ,for example: when {} else {} */
  trait UserModule(using parent: GlobalInfo) extends UserModuleOps, UserModuleDecls {
    val classSimpleName = this.getClass.getCanonicalName.split('.').last.mkString

    val thisClassName =
      (Try(classSimpleName) match {
        case Failure(exception) => "noName"
        case Success(value)     => value
      }) + naming.getIdWithDash

    /** Name for this Instance after new class.. */
    val thisInstanceName = naming.mkUidFrom(thisClassName)
    if (global.debugVerbose)
      println(s"new inst $thisInstanceName for $thisClassName")

    given moduleData: ModuleData =
      ModuleData(className = thisClassName, instanceName = thisInstanceName)
    // def name = this.getClass.getCanonicalName.split('.').last
    val globalInfo = parent

    def pushCmd(cmd: Cmds) = {
      moduleData.commands.append(cmd)
    }

    def pushBlk(ctr: Ctrl)(block: => Any) = {
      val uid = naming.getIntId
      pushCmd(sequentialCommands.Start(ctr, uid))
      block
      pushCmd(sequentialCommands.End(ctr, uid))
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
    // (r, di)
    r
  }

}
