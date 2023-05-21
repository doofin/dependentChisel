package dependentChisel.tests

import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*

import dependentChisel.codegen.compiler.*
import dependentChisel.typesAndSyntax.chiselModules.*

import scala.compiletime.*
import scala.compiletime.ops.int.*
import dependentChisel.typesAndSyntax.varDecls.newIO

object adder extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val (mod, depInfo: GlobalInfo) = makeModule { implicit p =>
      // new AdderCall1
      // new DoubleAdder3(2)
      new AdderComb4
      // new AdderTypeParm1[1]
      // new AdderTypeParm3
    }
    val fMod = chiselMod2firrtlCircuits(mod)
    // pp(fMod.modules map (_.modInfo))
    val firCirc = firrtlCircuits2str(fMod)
    println(firCirc)

    val verilog = firrtlUtils.firrtl2verilog(firCirc)
    // println(verilog)
  }

  class Adder1(using GlobalInfo) extends UserModule {
    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    y := a - b
  }

  class Adder2(using GlobalInfo) extends UserModule {
    val a = newIO[2](VarType.Input)
    val b = newIO[2](VarType.Input)
    val y = newIO[2](VarType.Output)

    y := a - b
  }

// not work
  class AdderTypeParm1[I <: Int: ValueOf](using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInput[I]("a")
    val b = newInput[I]("b")
    val y = newOutput[I]("y")

    println(constValueOpt[I]) // is none

    y := a + b
  }

// works if with inline
  inline def adderTypeParam2[I <: Int: ValueOf](using mli: ModLocalInfo) = {
    val a = newIO[I](VarType.Input)
    val b = newIO[I](VarType.Input)
    val y = newIO[I](VarType.Output)
    y := a + b
  }

  /** works with inline methods */
  class AdderTypeParm3(using parent: GlobalInfo) extends UserModule {
    adderTypeParam2[10]
  }

  class DoubleAdder3(val size: Int)(using parent: GlobalInfo) extends UserModule {
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

  class AdderCall1(using parent: GlobalInfo) extends UserModule {
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

  class AdderComb4(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val c = newInput[2]("c")
    val d = newInput[2]("d")
    val y = newOutput[2]("y")

    val m1 = newMod(new Adder1) // might be able to rm this
    val m2 = newMod(new Adder1) // might be able to rm this
    // m1.y := a - b // will both err
    m1.a := a
    m1.b := b
    m2.a := c
    m2.b := d

    y := m1.y + m2.y
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
