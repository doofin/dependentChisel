package dependentChisel.staticAnalysis
import dependentChisel.codegen.seqCommands.*
import dependentChisel.algo.seqCmd2tree.AST
import dependentChisel.typesAndSyntax.typesAndOps.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScalaCross
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

import dependentChisel.codegen.compiler.*
import tests.ifTest.*

import algo.seqCmd2tree.*

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.codegen.firrtlTypes.FirrtlCircuit

import dependentChisel.typesAndSyntax.control

object progGraph extends mainRunnable {

  def run = {
    // (1, 2, 3).mapConst((x: Int) => x * 2)
    val (mod, globalCircuit) = makeModule { implicit p =>
      new IfElse1
    // new IfModNested
    // new IfModDangling // ok
    }
    // ppc(mod.modLocalInfo.commands)
    // val newValue = chiselMod2tree(mod)
    // ppc(newValue)
    // pp(ast2progGraph(0, newValue))
  }

  override def main(args: Array[String]): Unit = run

  type pg = List[(Int, AtomicCmds | Expr[?], Int)]

  /* stmt2progGraph((0, stmtInput, -1), List(0, -1)) */
  def ast2progGraph(p: Int, ast: AST): pg = {
    /*     ast.value match {
      case x: AtomicCmds => List((p, x, q)): pg
      case x: Ctrl =>
        x match {
          case Ctrl.If(cond) =>
            val i = usedList.max
            val q1 = i + 1
            val q2 = i + 2
            val ul = usedList ++ List(q1, q2)
            val r: pg = List((p, cond.expr, q1)) ++ ast2progGraph(q1, q, x, ul)
          case Ctrl.Else() =>
          case Ctrl.Top()  =>
        }
    } */
    val q: Int = p + 1
    ast.value match {
      case x: AtomicCmds => List((p, x, q)): pg
      case x: Ctrl =>
        x match {
          case Ctrl.Top() =>
            val r = ast.cld.zipWithIndex flatMap ((x, i) => ast2progGraph(q + i, x))
            List((p, Skip, q)) ++ r.toList
          case _ =>
            val r = ast.cld.zipWithIndex flatMap ((x, i) => ast2progGraph(q + i, x))
            List((p, Skip, q)) ++ r.toList
        }

    }
  }
}
