package dependentChisel

import firrtl.*
import firrtl.ir.*
import firrtl.passes.*
import firrtl.transforms.*

import com.doofin.stdScalaJvm.*
import com.doofin.stdScala.mainRunnable

object firrtlTests extends mainRunnable {

  override def main(args: Array[String]): Unit = {}

  def run = {
    Module(NoInfo, "Adder", Seq(), Block(Seq()))
    // Circuit(NoInfo, Seq(Module(NoInfo, Seq(Port(NoInfo,null,null )), "Adder")
  }

  def Chirrtl1 = {
    /*
  case class GCD() extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(32.W))
    val b = Input(UInt(32.W))
    val e = Input(Bool())
    val z = Output(UInt(32.W))
    val v = Output(Bool())
  })
  val x = Reg(UInt(32.W))
  val y = Reg(UInt(32.W))

//  use a override method?
  when(x > y) {
    x := x -% y
  }
    .otherwise {
      y := y -% x
    }
  when(io.e) {
    x := io.a;
    y := io.b
  }
  io.z := x
  io.v := y === 0.U
}

     */
    """circuit GCD :
  module GCD :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip a : UInt<32>, flip b : UInt<32>, flip e : UInt<1>, z : UInt<32>, v : UInt<1>}

    reg x : UInt<32>, clock with :
      reset => (UInt<1>("h0"), x) @[GCD.scala 19:14]
    reg y : UInt<32>, clock with :
      reset => (UInt<1>("h0"), y) @[GCD.scala 20:14]
    node _T = gt(x, y) @[GCD.scala 23:10]
    when _T : @[GCD.scala 23:15]
      node _x_T = sub(x, y) @[GCD.scala 24:12]
      node _x_T_1 = tail(_x_T, 1) @[GCD.scala 24:12]
      x <= _x_T_1 @[GCD.scala 24:7]
    else :
      node _y_T = sub(y, x) @[GCD.scala 27:14]
      node _y_T_1 = tail(_y_T, 1) @[GCD.scala 27:14]
      y <= _y_T_1 @[GCD.scala 27:9]
    when io.e : @[GCD.scala 29:14]
      x <= io.a @[GCD.scala 30:7]
      y <= io.b @[GCD.scala 31:7]
    io.z <= x @[GCD.scala 33:8]
    node _io_v_T = eq(y, UInt<1>("h0")) @[GCD.scala 34:13]
    io.v <= _io_v_T @[GCD.scala 34:8]
"""
  }

  def circuit1 = """circuit GCD :
  module GCD :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip a : UInt<32>, flip b : UInt<32>, flip e : UInt<1>, z : UInt<32>, v : UInt<1>}

    reg x : UInt<32>, clock with :
      reset => (UInt<1>("h0"), x) @[GCD.scala 19:14]
    reg y : UInt<32>, clock with :
      reset => (UInt<1>("h0"), y) @[GCD.scala 20:14]
    node _T = gt(x, y) @[GCD.scala 23:10]
    when _T : @[GCD.scala 23:15]
      node _x_T = sub(x, y) @[GCD.scala 24:12]
      node _x_T_1 = tail(_x_T, 1) @[GCD.scala 24:12]
      x <= _x_T_1 @[GCD.scala 24:7]
    else :
      node _y_T = sub(y, x) @[GCD.scala 27:14]
      node _y_T_1 = tail(_y_T, 1) @[GCD.scala 27:14]
      y <= _y_T_1 @[GCD.scala 27:9]
    when io.e : @[GCD.scala 29:14]
      x <= io.a @[GCD.scala 30:7]
      y <= io.b @[GCD.scala 31:7]
    io.z <= x @[GCD.scala 33:8]
    node _io_v_T = eq(y, UInt<1>("h0")) @[GCD.scala 34:13]
    io.v <= _io_v_T @[GCD.scala 34:8]
"""
  def Circuit1r = {

// Parse the input
    val parsed: Circuit = firrtl.Parser.parse(circuit1)
    pp(parsed)
    val state = CircuitState(parsed, UnknownForm)

// Designate a series of transforms to be run in this order
    val transforms: Seq[Transform] =
      Seq(ToWorkingIR, ResolveKinds, InferTypes, new InferWidths)

// Run transforms and capture final state
// Emit output
    // val finalState = transforms.foldLeft(state) { (c: CircuitState, t: Transform) =>
    //   t.runTransform(c)

    // }
    // println(finalState.circuit.serialize)
  }

}
