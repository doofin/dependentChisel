package dependentChisel

import dependentChisel.typesAndSyntax.typesAndOps.Lit
import dependentChisel.typesAndSyntax.typesAndOps.VarLit
import dependentChisel.codegen.sequentialCommands.*
import dependentChisel.staticAnalysis.unInitAnalysis

import com.doofin.stdScalaCross.*

class unInitAnalysisSuite extends munit.FunSuite {
  test("unInitAnalysis test") {

    // each var is uninitialized at the beginning
    val initMap: Map[String, Boolean] =
      Map(
        "xyz".toCharArray
          .map(x => x.toString() -> false)
          .toList*
      )

    /* a program graph
0 -> x:=.. ->1 -> 3 -> 5
0 ->   2   -> 4
     */
    val pg =
      List(
        (0, WeakStmt(VarLit("x"), ":=", Lit[1](1)), 1),
        (1, Skip, 3), // x is initialized from 0->1->3
        (0, Skip, 2), // x is not initialized from 0->2
        (2, Skip, 4), // x is not initialized from 0->2->4
        (3, Skip, 5) // x is initialized from 0->1->3->5
      )
    val monoF = unInitAnalysis.mMonoFramework(initMap)

    val res = monoF.runWithProgGraph(pg)
    val expectedInit = Set(1, 3, 5)
    val resultInit = res.filter { case (k, v) =>
      v("x") // filter where x is true
    }.keySet

    pp(res)
    assertEquals(resultInit, expectedInit, "so x is only initialized at point 1,3,5")

  }
}
