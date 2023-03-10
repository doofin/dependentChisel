package dependentChisel.typesAndSyntax

import dependentChisel.syntax.ImperativeModules.ModLocalInfo
import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.syntax.ImperativeModules.*
/* control structures like switch */
object control {
  trait UserModuleOps { ut: UserModule =>
    def If[w <: Int](b: Bool[w])(block: => Any) = pushBlk(s"if")(block) //  $b

    def IfElse[w <: Int](b: Bool[w])(block: => Any)(block2: => Any) = {
      If(b)(block)
      pushBlk("else")(block2)
    }
  }

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
