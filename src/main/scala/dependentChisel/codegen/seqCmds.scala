package dependentChisel.codegen

import dependentChisel.typesAndSyntax.basicTypes.*
import com.doofin.stdScalaCross.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.global

/** sequential commands as in chisel UserModule */
object seqCmds {
  type Uid = Int

  /** control structures like if */
  enum Ctrl {
    case If(cond: BoolEx[?])
    // case IfElse[w <: Int](b: Bool[w])
    case Else[w <: Int]()
    case Top()
  }

  /** sequential commands type as in chisel UserModule */
  sealed trait Cmds
  case class Start[CT <: Ctrl](ctrl: CT, uid: Uid) extends Cmds
  case class End[CT <: Ctrl](ctrl: CT, uid: Uid) extends Cmds
  case class FirStmt(
      lhs: Var[?],
      op: String,
      rhs: Expr[?],
      prefix: String = "" // prefix can be node
  ) extends Cmds {
    override def toString(): String = { prefix + lhs.getname + s" $op $rhs" }
  }

}
