import dependentChisel.typesAndSyntax.typesAndOps.Lit
import dependentChisel.typesAndSyntax.typesAndOps.VarLit
import dependentChisel.codegen.seqCommands.*
import dependentChisel.staticAnalysis.checkUnInitAnalysis

import com.doofin.stdScalaCross.*

val varlist = "xyz"
// Some(bool)
// initialize a default value for each var
val initMap: Map[String, Boolean] =
  Map(
    varlist.toCharArray
      .map(x => x.toString() -> false)
      .toList*
  )

/*
0 -> x:=.. ->1 -> 3 -> 5
0 ->   2   -> 4
 */
val pg =
  List(
    (0, FirStmt(VarLit("x"), ":=", Lit[1](1)), 1),
    (1, Skip, 3),
    (0, Skip, 2),
    (2, Skip, 4),
    (3, Skip, 5)
  )
val mf = checkUnInitAnalysis.MonoFramework(initMap)

pp(mf.runWithProgGraph(pg))
