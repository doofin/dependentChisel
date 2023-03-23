package dependentChisel.typesAndSyntax

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*

import dependentChisel.codegen.seqCmdTypes.*

import dependentChisel.typesAndSyntax.chiselModules.*

/* control structures like switch
use a different trait to split
assign := is in statements */
object control {
  trait UserModuleOps { ut: UserModule =>
    def If[w <: Int](b: BoolEx[w])(block: => Any): Unit =
      pushBlk(Ctrl.If(b))(block) //  $b

    def IfElse[w <: Int](
        b: BoolEx[w]
    )(block: => Any)(block2: => Any): Unit = {
      pushBlk(Ctrl.If(b))(block)
      pushBlk(Ctrl.Else())(block2)
    }

    def newMod(newM: UserModule) = {
      pushCmd(newInst("", newM.thisClassName))
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
