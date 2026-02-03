package dependentChisel.staticAnalysis

import dependentChisel.typesAndSyntax.typesAndOps.*
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
    val initFinal = (0, 5)
    /* a program graph
0 -> x:=.. ->1 -> 3 -> 5
0 ->   2   -> 4
     */
    val pg =
      List(
        (0, WeakStmt(VarLit("x"), ":=", Lit[1](1)), 1),
        (1, Skip, 3), // x is initialized from 0->1->3
        (0, WeakStmt(VarLit("y"), ":=", Lit[1](1)), 2), // x is not init from 0->2, y yes
        (2, Skip, 4), // x not, y yes
        (3, Skip, 5), // x is initialized from 0->1->3->5
        (4, Skip, 5)
      )
    val monoF = unInitAnalysis.monoFramework(initMap)

    val res = monoF.runWithProgGraph(pg, entryExitPoint = initFinal)
    val expectedInit = Set(1, 3, 5)
    val resultInitX = res.filter { case (k, v) =>
      v("x") // filter where x is true
    }.keySet

    pp(res)
    assertEquals(resultInitX, expectedInit)

    val expectedInitY = Set(2, 4, 5)
    val resultInitY = res.filter { case (k, v) =>
      v("y") // filter where y is true
    }.keySet
    assertEquals(resultInitY, expectedInitY)

  }
}
