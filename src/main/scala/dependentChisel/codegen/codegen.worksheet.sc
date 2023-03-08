import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.syntax.monadicAST.BinOp
import dependentChisel.codegen.genFirrtl.*
import dependentChisel.codegen.firAST.*
import com.doofin.stdScalaCross.*

// ok
val r = toANF(FirStmt("y", ":=", Lit(1) + Lit(2) + Lit(3)))
pp(r)

// ok
val r2 = toANF(FirStmt("y", ":=", Lit(1) + Lit(2) + Lit(3) + Lit(4)))
pp(r2)
