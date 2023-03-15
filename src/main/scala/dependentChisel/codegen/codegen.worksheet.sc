import dependentChisel.*
import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.syntax.monadicAST.BinOp
import dependentChisel.codegen.firAST.*
import dependentChisel.codegen.firAST.*
import com.doofin.stdScalaCross.*
import tests.ifTest.*

import algo.seqCmd2tree.*

import dependentChisel.syntax.ImperativeModules.*
/*

implicit def str2lit(s: String): VarLit[Nothing] = VarLit(s)
// ok
pp(toANF(FirStmt("y", ":=", Lit(1) + Lit(2))))
val r = toANF(FirStmt("y", ":=", Lit(1) + Lit(2) + Lit(3)))
pp(r)

// ok
val r2 = toANF(FirStmt("y", ":=", Lit(1) + Lit(2) + Lit(3) + Lit(4)))
pp(r2)
 */

tests.ifTest.run

val (mod, depInfo) = makeModule { implicit p =>
//   new IfElse1
  new IfModNested // ok
}

// pp(mod.modLocalInfo)
val cmds = mod.modLocalInfo.commands
pp(cmds)
val anf = cmd2ANF(cmds.toList)
pp(anf)
val tree = list2tree(anf)
pp(tree)

pp(tree2str(tree))

/* ok for IfModNested
    y := a - b
    If(a === b) {
      y := a + b
      If(a === c) {
        y := a * b
      }
    } */
