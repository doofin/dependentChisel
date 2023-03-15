package dependentChisel.codegen

import dependentChisel.typesAndSyntax.basicTypes.*
import com.doofin.stdScalaCross.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.global

object seqCmds {
  type Uid = Int
  enum Ctrl {
    case If[w <: Int](b: Bool[w])
    // case IfElse[w <: Int](b: Bool[w])
    case Else[w <: Int]()
    case Top()
  }

  sealed trait Cmds
  case class Start[CT <: Ctrl](ctrl: CT, uid: Uid) extends Cmds
  case class End[CT <: Ctrl](ctrl: CT, uid: Uid) extends Cmds
  case class FirStmt(
      lhs: Var[?],
      op: String,
      rhs: Expr[?],
      prefix: String = ""
  ) extends Cmds {
    override def toString(): String = { lhs.getname + op + rhs }
  } // prefix can be node

  /*
  // cmdname,uid,cond

  enum SeqCmds {
    case Start[CT <: Ctrl](ctrl: CT, uid: Uid)
    // case Item(item: FirStmt)
  }

   */
}
