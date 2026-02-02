package dependentChisel

import com.doofin.stdScalaCross.*

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.codegen.sequentialCommands.*
import dependentChisel.staticAnalysis.liveVarAnalysis

class liveVarAnalysisSuite extends munit.FunSuite {
  test("test free variable function") {
    val expr = VarLit("x") + VarLit("y") + Lit[8](8)
    val expr2 = UniOp(expr + VarLit("z"), "neg")

    val fvResult = liveVarAnalysis.fv(expr2)
    val expectedFv = Set("x", "y", "z")
    assertEquals(fvResult, expectedFv, "free var should be x,y,z")
  }

  test("live variable transfer function test") {

    // r:= r - y
    val stmt1 = (0, WeakStmt(VarLit("r"), ":=", VarLit("r") - VarLit("y")), 1)
    // {}-{r}+{r,y} = {r,y} for l-kill + gen
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
        (2, WeakStmt(VarLit("y"), ":=", VarLit("y") + Lit[1](1)), 4),
        (3, WeakStmt(VarLit("z"), ":=", VarLit("x") + VarLit("y") + Lit[1](1)), 5),
        (4, Skip, 5)
      )

    val entryExitPoint = (0, 5)
    /*
    ASCII visualization:

                0
             /    \
          (x:=1)   (y:=1)
           |         \
           v          v
           1          2
           |          | (y:=y+1)
           v          v
           3          4
           |
      (z:=x+y+1)   /
           |      /
           v     /
          
           5
     */
    val monoF = liveVarAnalysis.monoFramework()

    // live variable analysis is a backward analysis
    val res = monoF.runWithProgGraph(
      pg, //
      isForward = false,
      entryExitPoint
    )

    val expected = Map(
      0 -> Set("y"),
      5 -> Set(),
      1 -> Set("x", "y"),
      2 -> Set("y"),
      3 -> Set("x", "y"),
      4 -> Set()
    )
    assertEquals(res, expected)

  }
}
