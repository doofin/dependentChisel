package dependentChisel.codegen
import scala.collection.mutable

import com.doofin.stdScalaCross.*

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.codegen.seqCommands.*

/** various checks like checking width */
object typeCheck {

  /** return if width check is ok,or the width of expr */
  private def getExprWidth(
      typeMap: mutable.Map[Expr[?] | Var[?], Option[Int]],
      expr: Expr[?]
  ): Int = {
    val tm = typeMap
    expr match {
      case BinOp(a, b, nm) =>
        val (i, j) = (getExprWidth(typeMap, a), getExprWidth(typeMap, b))
        val isWidthEqu = i == j
        if (!isWidthEqu) {
          dbg(a, b, nm)
          assert(false, "checkWidth find Width mismatch inside expr ! ")
        }
        i

      // case VarLit(name)     =>
      // case ExprAsBool(expr) =>
      // case VarDymTyped(width, tp, name) =>
      // case VarTyped(name)               =>
      // case Input(name)                  =>
      // case Output(name)                 =>
      case Lit(i) => i
      // case LitDym(i)                    =>
      case x =>
        tm(x).get
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
      case FirStmt(lhs, op, rhs, prefix) =>
        val lr = (getExprWidth(typeMap, lhs), getExprWidth(typeMap, rhs)) match {
          // only check if both result are numbers
          case lrWidth @ (i, j) =>
            val widthEqu = i == j
            if (!widthEqu) {
              println("checkWidth find Width mismatch for := ! ")
              dbg(lrWidth)
            }
            widthEqu

        }
        // checkExprWidth(typeMap, rhs)
        // (lr, checkExprWidth(typeMap, rhs))

        lr

      case x => true
      // case NewInstStmt(instNm, modNm)    =>
      // case VarDecls(v)                   =>
      // case Skip                          =>
    }
  }

}
