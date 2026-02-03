package dependentChisel.codegen

import dependentChisel.typesAndSyntax.typesAndOps.*
import com.doofin.stdScalaCross.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.global

/** sequential commands used in chisel UserModule to build circuit
  *
  * nested control structures are implemented using start/end commands which is implicitly a stack,
  * so we can later convert it to a tree with seqCmd2tree
  */
object sequentialCommands {
  type Uid = Int

  /** control structures like if */
  enum Ctrl {
    case If(cond: Bool)
    // case IfElse[w <: Int](b: Bool[w])
    case Else[w <: Int]()
    case Top() // represents top level
  }

  /** all sorts of sequential commands
    */
  sealed trait Cmds

  /** atomic commands like decl,assign,etc */
  sealed trait AtomicCmds extends Cmds

  /** represent start/end of control block
    *
    * @param ctrl
    * @param uid
    */
  case class Start[CT <: Ctrl](ctrl: CT, uid: Uid) extends Cmds // uid is not used
  case class End[CT <: Ctrl](ctrl: CT, uid: Uid) extends Cmds

  /** atomic commands like decl,assign,etc */

  /** new inst for a module */
  case class NewInstance(instNm: String, modNm: String) extends AtomicCmds

  /** firrtl statements: weakly typed which doesn't require width of lhs = wid of rhs.
    */
  case class WeakStmt(
      lhs: Var[?],
      op: String,
      rhs: Expr[?],
      prefix: String = "" // prefix can be node
  ) extends AtomicCmds

  /** for Wire, Reg, and IO */
  case class VarDecls(v: VarDynamic) extends AtomicCmds
  case object Skip extends AtomicCmds
  /* TODO:also allow dym check which rm type sig of var[t] ,etc. cases
   * of (lhs,rhs) are (dym,stat),(dym,dym)....
   1.new super type for Var[w]
   */

  /** formal verification commands */
  case class BoolProp(name: String, prop: Bool) extends AtomicCmds

}
