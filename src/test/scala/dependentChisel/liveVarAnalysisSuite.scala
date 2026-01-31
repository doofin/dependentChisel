package dependentChisel

import com.doofin.stdScalaCross.*

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.codegen.sequentialCommands.*
import dependentChisel.staticAnalysis.liveVarAnalysis

class liveVarAnalysisSuite extends munit.FunSuite {
  test("test free variable function".ignore) {
    val expr = VarLit("x") + VarLit("y") + Lit[8](8)
    val expr2 = UniOp(expr + VarLit("z"), "neg")

    val fvResult = liveVarAnalysis.fv(expr2)
    val expectedFv = Set("x", "y", "z")
    assertEquals(fvResult, expectedFv, "free var should be x,y,z")
  }

  test("live variable transfer function test") {

    val stmt1 = (0, WeakStmt(VarLit("r"), ":=", VarLit("r") - VarLit("y")), 1)

    val t1 = liveVarAnalysis.transferLV(stmt1, Set())
    assertEquals(t1, Set("r", "y"), "after r:=r - y , r and y should be live ")

    val stmt2 = (0, WeakStmt(VarLit("x"), ":=", Lit[1](1)), 1)

    // x should be dead after assignment
    val t2 = liveVarAnalysis.transferLV(stmt2, Set("x", "y"))
    assertEquals(t2, Set("y"), "after x:=1, x should be dead ")

    // pp(res)

  }

  test("live variable analysis full test") {

    val pg =
      List(
        (0, WeakStmt(VarLit("x"), ":=", Lit[1](1)), 1),
        (1, Skip, 3),
        (0, WeakStmt(VarLit("y"), ":=", Lit[1](1)), 2),
        (2, Skip, 4),
        (3, WeakStmt(VarLit("z"), ":=", VarLit("x") + Lit[1](1)), 5),
        (4, Skip, 5)
      )

    // each var is uninitialized at the beginning
    val initMap =
      Set.empty[String]

    val entryExitPoint = (0, 5)
    /*
    ASCII visualization:

                0
             /    \
          (x:=1)   (y:=1)
           |       \
           v        v
           1        2
           |        |
           v        v
           3        4
           |
          (z:=x+1)
           |
           v   /
           5
     */
    val monoF = liveVarAnalysis.monoFramework(initMap)

    // live variable analysis is a backward analysis
    val res = monoF.runWithProgGraph(
      pg, //
      isForward = false,
      entryExitPoint
    )

    pp(res)

  }
}
