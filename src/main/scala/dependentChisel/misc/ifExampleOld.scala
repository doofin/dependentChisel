package dependentChisel.misc

/* imperativeStyle dependent chisel */
import dependentChisel.syntax.ImperativeModules.*
import dependentChisel.*
import chiselDataTypes.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.chiselDataTypes
import syntax.tree.*
import dependentChisel.syntax.tree

object ifExampleOld extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val d = makeModule { implicit p => new IfExample1 }
    val tp = d._1.create
    val thisTr = tree.tp2tr(tp.pr.toList)
    pp(thisTr)

  }

  class IfExample1(using parent: DependenciesInfo) extends UserModuleOld {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val y = newOutput[16]("y")
    val y2 = newOutput[16]("y2")

    override def create: TopLevelCircuit = new TopLevelCircuit {
      new If(a === b) {
        y := a + b
        y2 := a - b

        new Else {
          y := a - b
          y2 := a + b
          // new Else {}
          // need extra check to prevent Else{ Else {}}
        }
      }
    }
  }
}
