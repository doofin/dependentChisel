package dependentChisel.typesAndSyntax

import dependentChisel.syntax.ImperativeModules.ModLocalInfo
import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.syntax.ImperativeModules.*
import dependentChisel.codegen.seqCmds.Ctrl
/* control structures like switch */
object control {
  trait UserModuleOps { ut: UserModule =>
    def If[w <: Int](b: Bool[w])(block: => Any): Unit =
      pushBlk(Ctrl.If(b))(block) //  $b

    def IfElse[w <: Int](b: Bool[w])(block: => Any)(block2: => Any): Unit = {
      pushBlk(Ctrl.If(b))(block)
      pushBlk(Ctrl.Else())(block2)
    }
  }

  /* def switch[condW <: Int](using
      m: ModLocalInfo
  )(cond: Var[condW])(cases: (Expr[condW], Stmt)*) = {
    // nested stmt? exhaustive check?
    // m.io.prepend(s"input ${name}[${constValueOpt[w]}]")
    // case classs switch
    Stmt.StmtNoOp
  } */

  /* Combinational Circuits */
}
