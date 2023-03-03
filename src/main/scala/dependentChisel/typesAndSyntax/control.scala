package dependentChisel.typesAndSyntax

import dependentChisel.syntax.ImperativeModules.ModLocalInfo
import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
/* control structures like switch */
object control {
  def switch[condW <: Int](using
      m: ModLocalInfo
  )(cond: Var[condW])(cases: (Expr[condW], Stmt)*) = {
    // nested stmt? exhaustive check?
    // m.io.prepend(s"input ${name}[${constValueOpt[w]}]")
    // case classs switch
    Stmt.StmtNoOp
  }

  def when[condW <: Int](using
      m: ModLocalInfo
  )(cond: Var[condW])(stmts: Stmt*) = {
    Stmt.StmtNoOp
  }
  // def add[t <: Int](x: t, y: t) = {}
  // add(1, 2)
  /* Combinational Circuits */
}
