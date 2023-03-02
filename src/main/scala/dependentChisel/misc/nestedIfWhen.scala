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

object nestedIfWhen extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val d = makeModule { implicit p => new NestedWhen }
    val tp = d._1.create
    val thisTr = tree.tp2tr(tp.pr.toList)
    pp(thisTr)

    // pp(d.names.toList)
    // val outInfo = d.modules.toList.map(x => (x.name, x.modCircuits))
    // pp(outInfo)

  }

  class NestedWhen(using parent: DependenciesInfo) extends UserModuleOld {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val fn = newInput[2]("fn")
    val y = newOutput[16]("y")
    val z = newOutput[16]("y")
    // y := Lit(0)

    override def create: TopLevelCircuit = new TopLevelCircuit {
      new When("w1") {
        // new If("aa") {}
        y := a + b
        y := a - b
        new When("w1-1") {}.here
        new When("w1-2") {}.here
        new When("w2") {
          new When("w2-1") {
            z := a + b
          }.here
          new When("w2-2") {}.here
        }.here
      }.here
    }
  }

}
