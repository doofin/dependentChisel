package dependentChisel.staticAnalysis

import com.doofin.stdScalaCross.*

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.codegen.sequentialCommands.*
import dependentChisel.staticAnalysis.reachingDefAnalysis

class reachingDefAnalysisSuite extends munit.FunSuite {

  // program graph in PA appetizer,p17 and example 2.2 at p18
  val pg1 =
    List(
      (0, WeakStmt(VarLit("y"), ":=", Lit[1](1)), 1),
      (1, Skip, 2),
      (2, WeakStmt(VarLit("y"), ":=", VarLit("x") + VarLit("y")), 3),
      (3, WeakStmt(VarLit("x"), ":=", VarLit("x") - Lit[1](1)), 1),
      (1, Skip, 4)
    )
  test("reaching definition full test") {
    val initMap: reachingDefAnalysis.Domain = Set.empty
    val t1 = reachingDefAnalysis.transferFn(pg1(0), initMap)

    val mono = reachingDefAnalysis.monoFramework()
    val res = mono.runWithProgGraph(pg1, isForward = true, entryExitPoint = (0, 4))

    // from 2.12 p21, the result of Example 2.2
    val expected = Map(
      0 -> Set(),
      1 -> Set(("y", 0, 1), ("y", 2, 3), ("x", 3, 1)),
      2 -> Set(("y", 0, 1), ("y", 2, 3), ("x", 3, 1)),
      3 -> Set(("y", 2, 3), ("x", 3, 1)),
      4 -> Set(("y", 0, 1), ("y", 2, 3), ("x", 3, 1))
    )
    assertEquals(res, expected)
  }
}
