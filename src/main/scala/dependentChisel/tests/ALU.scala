package dependentChisel.tests

/* imperativeStyle dependent chisel */
import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

import dependentChisel.typesAndSyntax.chiselModules.*

import dependentChisel.typesAndSyntax.control
object ALU extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {}

  class ALU(using parent: GlobalInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val fn = newInput[2]("fn")
    val y = newOutput[16]("y")
    val z = newOutput[16]("y")
    // y := Lit(0)

    // stmt no effect there!see scaloid
    // new When("w1") {
    // y :== a + b|

    /* switch(fn)(
      // Lit(1) -> (y := a + b), // can't infer,bug?
      // Lit[2](1) -> (y := a + b) // ok
      // Lit(1) -> (y := a - b),
      // Lit[2](2) -> (y := a + b)
    ) */
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
