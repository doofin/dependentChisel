package dependentChisel.codegen

import dependentChisel.typesAndSyntax.typesAndOps.*
import com.doofin.stdScalaCross.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.global

/** sequential commands used in chisel UserModule to build circuit */
object seqCommands {
  type Uid = Int

  /** control structures like if */
  enum Ctrl {
    case If(cond: BoolEx[?])
    // case IfElse[w <: Int](b: Bool[w])
    case Else[w <: Int]()
    case Top()
  }

  /** all sorts of sequential commands */
  sealed trait Cmds
  case class Start[CT <: Ctrl](ctrl: CT, uid: Uid) extends Cmds
  case class End[CT <: Ctrl](ctrl: CT, uid: Uid) extends Cmds

  /** for new mod */
  case class NewInstStmt(instNm: String, modNm: String) extends Cmds

  /* TODO:also allow dym check which rm type sig of var[t] ,etc. cases
   * of (lhs,rhs) are (dym,stat),(dym,dym)....
   1.new super type for Var[w]
   */

  /** firrtl statements: weakly typed which doesn't require width of lhs = wid
    * of rhs.
    */
  case class FirStmt(
      lhs: Var[?],
      op: String,
      rhs: Expr[?],
      prefix: String = "" // prefix can be node
  ) extends Cmds

  /** for Wire, Reg, and IO */
  case class VarDecls(prefix: String, tp: String, width: Int)
}
