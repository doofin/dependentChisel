package dependentChisel.codegen
import scala.collection.mutable

import com.doofin.stdScalaCross.*

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.codegen.seqCommands.*

/** various checks like checking width */
object typeCheck {

  /** return if width check is ok,or the width of expr */
  private def checkExprWidth(
      typeMap: mutable.Map[Expr[?] | Var[?], Option[Int]],
      expr: Expr[?]
  ): Boolean Either Int = {
    val tm = typeMap
    expr match {
      case BinOp(a, b, nm) =>
        (checkExprWidth(typeMap, a), checkExprWidth(typeMap, b)) match {
          case (Right(i), Right(j)) => Left(i == j)
          case _                    => ???
        }

      // case VarLit(name)     =>
      // case ExprAsBool(expr) =>
      // case VarDymTyped(width, tp, name) =>
      // case VarTyped(name)               =>
      // case Input(name)                  =>
      // case Output(name)                 =>
      // case Lit(i)                       =>
      // case LitDym(i)                    =>
      case x => Right(tm(x).get)
    }
  }

  def checkWidth(
      typeMap: mutable.Map[Expr[?] | Var[?], Option[Int]],
      cmds: AtomicCmds
  ) = {
    cmds match {
      /* 1.add width field in FirStmt
        2. add width in lhs var and rhs expr
        3. use a map to store width of var and expr */
      case FirStmt(lhs, op, rhs, prefix) => checkExprWidth(typeMap, rhs)
      case x                             => x
      // case NewInstStmt(instNm, modNm)    =>
      // case VarDecls(v)                   =>
      // case Skip                          =>
    }
  }

}
