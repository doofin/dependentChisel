package dependentChisel.codegen
import dependentChisel.typesAndSyntax.basicTypes.*
import com.doofin.stdScalaCross.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.global

object seqCmds {
  sealed trait Cmds
  case class Block(s: String) extends Cmds // control structure
  // case class Start() extends Cmds
  case class FirStmt(
      lhs: Var[?],
      op: String,
      rhs: Expr[?],
      prefix: String = ""
  ) extends Cmds // prefix can be node
}
