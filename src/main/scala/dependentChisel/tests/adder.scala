package dependentChisel.tests

import dependentChisel.*

import com.doofin.stdScalaCross.*
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
object adder extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val (mod, depInfo: GlobalInfo) = makeModule { implicit p =>
      new DoubleAdder
    // new DoubleAdder3(2)
    }
    val fMod = chiselMod2firrtlCircuits(mod)
    // pp(fMod.modules map (_.modInfo))
    val firCirc = firrtlCircuits2str(fMod)
    // println(firCirc)

    val verilog = firrtlUtils.firrtl2verilog(firCirc)
    println(verilog)
  }

  class Adder1(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    y := a - b
  }

  class DoubleAdder2[I <: Int](using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInput[I]("a")
    val b = newInput[I]("b")
    // val c = newInput[2]("c")
    // val d = newInput[2]("d")
    val y = newOutput[I]("y")

    // val m1 = newMod(new Adder1) // might be able to rm this
    // m1.y := a - b // will both err
    y := a + b
  }

  class DoubleAdder3(val size: Int)(using parent: GlobalInfo)
      extends UserModule {
// parent contains global info

    val a = newInput[size.type]("a") // val a = newInput[size.type]("a",size)
    val b = newInput[size.type]("b")
    // val s2 = ""
    // newInput[s2.type]("b")
    // val c = newInput[2]("c")
    // val d = newInput[2]("d")
    val y = newOutput[size.type]("y")

    // val m1 = newMod(new Adder1) // might be able to rm this
    // m1.y := a - b // will both err
    y := a + b
  }

  class DoubleAdder(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    // val c = newInput[2]("c")
    // val d = newInput[2]("d")
    val y = newOutput[2]("y")

    val m1 = newMod(new Adder1) // might be able to rm this
    // m1.y := a - b // will both err
    m1.a := a
    m1.b := b
    y := m1.y
  }

  /** untyped */
  class UAdder1(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = VarDymTyped(1, VarDeclTp.Input)
    // val b = newInput[2]("b")
    // val y = newOutput[2]("y")
//
    a := a - a
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
