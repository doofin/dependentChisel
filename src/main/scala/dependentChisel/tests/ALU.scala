package dependentChisel.tests

/* imperativeStyle dependent chisel */
import dependentChisel.syntax.ImperativeModules.*
import dependentChisel.*
import chiselDataTypes.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.chiselDataTypes
import syntax.tree.*
import dependentChisel.syntax.tree

object ALU extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val d = makeModule { implicit p => new ALU }
    val tp = d._1.create
    val thisTr = tree.tp2tr(tp.pr.toList)
    pp(thisTr)

    // pp(d.names.toList)
    // val outInfo = d.modules.toList.map(x => (x.name, x.modCircuits))
    // pp(outInfo)

  }
  /*   def circuit1(x: Input[_], y: Input[_]) = {
    new TopLevel { new When { x := y }.here }
  } */

  class ALU(using parent: DependenciesInfo) extends UserModuleOld {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val fn = newInput[2]("fn")
    val y = newOutput[16]("y")
    val z = newOutput[16]("y")
    // y := Lit(0)

    // stmt no effect there!see scaloid
    // new When("w1") {
    // y :=== a + b|
    override def create: TopLevelCircuit = {
      new TopLevelCircuit {
        new When("w1") {
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

    switch(fn)(
      // Lit(1) -> (y := a + b), // can't infer,bug?
      Lit[2](1) -> (y :== a + b) // ok
      // Lit(1) -> (y := a - b),
      // Lit[2](2) -> (y := a + b)
    )
    // a.getLit(1)
  }

}
/*
class Alu extends Module {
val io = IO(new Bundle {
val a = Input(UInt (16.W))
val b = Input(UInt (16.W))
val fn = Input(UInt (2.W))
val y = Output (UInt (16.W))
})
// some default value is needed
io.y := 0.U
// The ALU selection
switch (io.fn) {
is (0.U) { io.y := io.a + io.b }
is (1.U) { io.y := io.a - io.b }
is (2.U) { io.y := io.a | io.b }
is (3.U) { io.y := io.a & io.b }
}
}
 */
