package dependentChisel.tests

import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

import dependentChisel.codegen.compiler.*
import tests.ifTest.*

import algo.seqCmd2tree.*

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.codegen.firrtlTypes.FirrtlCircuit

object adder extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val (mod, depInfo: GlobalInfo) = makeModule { implicit p =>
      new AdderDouble
    }
    val fMod = chiselMod2firrtlCircuits(mod)
    val firCirc = firrtlCircuits2str(fMod)
    println(firCirc)

    // firrtlUtils.firrtl2verilog(firCirc)
  }

  class Adder1(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    y := a - b
  }

  class AdderDouble(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    val m1 = new Adder1 // need to gen inst of
    m1.y := a - b
  }

}

/*
circuit DoubleAdder :
  module Adder :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip a : UInt<32>, flip b : UInt<32>, y : UInt<32>}

    node _io_y_T = add(io.a, io.b) @[doubleAdder.scala 19:16]
    node _io_y_T_1 = tail(_io_y_T, 1) @[doubleAdder.scala 19:16]
    io.y <= _io_y_T_1 @[doubleAdder.scala 19:8]

  module DoubleAdder :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip a : UInt<32>, flip b : UInt<32>, flip c : UInt<32>, flip d : UInt<32>, y : UInt<32>}

    inst m1 of Adder @[doubleAdder.scala 34:18]
    m1.clock <= clock
    m1.reset <= reset
    inst m2 of Adder @[doubleAdder.scala 35:18]
    m2.clock <= clock
    m2.reset <= reset
    m1.io.a <= io.a @[doubleAdder.scala 36:11]
    m1.io.b <= io.b @[doubleAdder.scala 37:11]
    m2.io.a <= io.c @[doubleAdder.scala 38:11]
    m2.io.b <= io.d @[doubleAdder.scala 39:11]
    node _io_y_T = add(m1.io.y, m2.io.y) @[doubleAdder.scala 41:19]
    node _io_y_T_1 = tail(_io_y_T, 1) @[doubleAdder.scala 41:19]
    io.y <= _io_y_T_1 @[doubleAdder.scala 41:8]
 */
