import dependentChisel.typesAndSyntax.typesAndOps.VarLit
import dependentChisel.codegen.seqCommands.*
import dependentChisel.staticAnalysis.checkUnInitAnalysis

val varlist = "xyz"
val initMap =
  Map(
    varlist.toCharArray
      .map(x => x.toString() -> false)
      .toList*
  )

/*
0 -> x:=.. ->1 -> 3
                    -> 5,6
0 ->   2   -> 4
 */
val pg =
  List(
    (0, FirStmt(VarLit("x"), ":=", null), 1),
    (0, Skip, 2),
    (1, Skip, 3),
    (3, Skip, 5),
    (3, Skip, 6),
    (2, Skip, 4)
  )
val mf = checkUnInitAnalysis.MonoFramework(initMap)

mf.runWithProgGraph(pg)
