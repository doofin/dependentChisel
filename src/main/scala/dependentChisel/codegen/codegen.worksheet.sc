import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.syntax.monadicAST.BinOp
import dependentChisel.codegen.genFirrtl.*
import dependentChisel.codegen.firAST.*
import com.doofin.stdScalaCross.*

implicit def str2lit(s: String): VarLit[Nothing] = VarLit(s)
// ok
pp(toANF(FirStmt("y", ":=", Lit(1) + Lit(2))))
val r = toANF(FirStmt("y", ":=", Lit(1) + Lit(2) + Lit(3)))
pp(r)

// ok
val r2 = toANF(FirStmt("y", ":=", Lit(1) + Lit(2) + Lit(3) + Lit(4)))
pp(r2)
