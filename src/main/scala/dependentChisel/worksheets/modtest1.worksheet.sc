//Make pass/fail tests comparing to chisel example,easier to read,chisel conventions

import com.doofin.stdScalaCross.*
import chisel3.*

import chisel3.ExplicitCompileOptions.Strict
import chisel3.internal.sourceinfo.UnlocatableSourceInfo
import chisel3.stage.ChiselGeneratorAnnotation
import chisel3.internal.sourceinfo.SourceInfo
import firrtl.AnnotationSeq

implicit val srcIfo: SourceInfo = UnlocatableSourceInfo
implicit val compOpt: CompileOptions = Strict

/* example:adder
similar to https://github.com/ucb-bar/chisel-tutorial/blob/release/src/main/scala/examples/FullAdder.scala
 */

import dependentChisel.syntax.bundles.*

//correct, in chisel2
case class AdderBundle() extends Bundle { // as a named class
  val a = Input(new chisel3.UIntFactory {}.apply(8.W)) // UInt(8.W) won't work
  //  due to duplicate def from pkg obj and toplevel
  val b = Input(new chisel3.UIntFactory {}.apply(8.W)) //
  val y = Output(new chisel3.UIntFactory {}.apply(8.W))
}

case class Adder() extends Module {
  val io = IO(AdderBundle())
  io.y := io.a do_+ io.b // io.y := io.a + io.b
}

//correct, in dep chisel (this project)

case class MyAdderBundle1() extends MyBundle[MyAdderBundle1] {
  val a = UIntDep[8]().asInput
  val b = UIntDep[8]().asInput
  val y = UIntDep[8]().asOutput
}

case class MyAdder1() extends MyModule[MyAdder1] {
  val bundle1 = MyAdderBundle1()
  bundle1.y := bundle1.a + bundle1.b
}

//error, in dep chisel (this project)
case class MyAdderBundle2() extends MyBundle[MyAdderBundle2] {
  val a = UIntDep[8]().asInput
  val b = UIntDep[8]().asInput
  val y = UIntDep[9]().asOutput
}

case class MyAdder2() extends MyModule[MyAdder2] {
  val bundle1 = MyAdderBundle2()
//   bundle1.y := bundle1.a + bundle1.b
}

MyAdderBundle2().getTp

// example:gcd
// in chisel2
// in dep chisel (this project)
