package dependentChisel.misc

import chisel3.*
// import chisel3.util._

import chisel3.ExplicitCompileOptions.Strict
import chisel3.internal.sourceinfo.UnlocatableSourceInfo
import chisel3.stage.ChiselGeneratorAnnotation
import chisel3.internal.sourceinfo.SourceInfo
import firrtl.AnnotationSeq

import dependentChisel.*
import depTypes.*
import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import bundles.*

import dependentChisel.misc.bundles
object chiselAdder extends mainRunnable {
  implicit val srcIfo: SourceInfo = UnlocatableSourceInfo
  implicit val compOpt: CompileOptions = Strict

  /* example:a very simpole adder where y:=a+b*/
// in chisel2
// similar to https://github.com/ucb-bar/chisel-tutorial/blob/release/src/main/scala/examples/FullAdder.scala

  case class AdderBundle() extends Bundle { // composite dep type to gather info from
    val a = Input(new chisel3.UIntFactory {}.apply(8.W))
    // UInt(8.W) won't work due to duplicate def from pkg obj and toplevel
    // val c = UInt(8.W)
    val b = Input(new chisel3.UIntFactory {}.apply(8.W)) //
    val y = Output(new chisel3.UIntFactory {}.apply(8.W))
  }

  case class Adder() extends Module {
    val io = IO(AdderBundle())
    io.y := io.a do_+ io.b // io.y := io.a + io.b
  }

// in dep chisel (this project)

  class MyBundle1() extends MyBundle[MyBundle1] {
    val a = UIntDep[8]().asInput // add more info for input
    val b = UIntDep[8]().asInput
    val y = UIntDep[8]().asOutput
  }

  case class MyMod2() { // extend Module
    val bundle1 = MyBundle1()

    def create = {
      bundle1.y := bundle1.a + bundle1.b // use create for now
    }
  }

  // (new MyMod2()).bundle1.a := 1.W

  def toFir(bundle: MyBundle1) = {
    // def toFir[BundleTp <: MyBundle[_]](bd: BundleTp) // not work yet,need reify?
    // macros.toFir(bundle)

  }
  // toFir(new MyBundle1())

  /* equality test:bundles with different setup will be checked in compile time */
  case class MyBundle2() extends MyBundle[MyBundle1] {
    val a = UIntDep[8]()
    val b = UIntDep[8]()
    val y = UIntDep[8]()
    // val v3 = List(1, 2, 3)
  }
  // macros.bundleEqu(MyBundle1(), MyBundle2())

  case class MyBundle3() extends MyBundle[MyBundle1] {
    val a = UIntDep[8]()
    val b = UIntDep[8]()
    val y = UIntDep[1]()
    // val v3 = List(1, 2, 3)
  }

  // macros.bundleEqu(MyBundle1(), MyBundle3()) // ok,will fail
  // class Module3(bundle: MyBundle1) {}
  // new Module3(MyBundle3())
  override def main(args: Array[String]): Unit = {}

}

/*
case class adder() extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(32.W))
    val b = Input(UInt(32.W))
    val y = Output(UInt(32.W))
  })
  io.y := io.a + io.b
}


circuit adder :
  module adder :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip a : UInt<32>, flip b : UInt<32>, y : UInt<32>}

    node _io_y_T = add(io.a, io.b) @[GCD.scala 88:16]
    node _io_y_T_1 = tail(_io_y_T, 1) @[GCD.scala 88:16]
    io.y <= _io_y_T_1 @[GCD.scala 88:8]
 */
