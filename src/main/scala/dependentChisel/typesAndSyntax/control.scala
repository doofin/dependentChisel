package dependentChisel.typesAndSyntax

import com.doofin.stdScalaCross.*

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*

import dependentChisel.codegen.seqCommands.*

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.syntax.naming
import dependentChisel.codegen.firrtlTypes.IOdef

import scala.compiletime.*

/* control structures like switch
use a different trait to split
assign := is in statements */
object control {
  trait UserModuleOps { ut: UserModule =>
    def If[w <: Int](b: ExprAsBool[w])(block: => Any): Unit =
      pushBlk(Ctrl.If(b))(block) //  $b

    def IfElse[w <: Int](
        b: ExprAsBool[w]
    )(block: => Any)(block2: => Any): Unit = {
      pushBlk(Ctrl.If(b))(block)
      pushBlk(Ctrl.Else())(block2)
    }

    def newMod[M <: UserModule](newMod: M) = {
      /*     m1.clock <= clock
    m1.reset <= reset */
      pushCmd(NewInstStmt(newMod.thisInstanceName, newMod.thisClassName))
      newMod
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
