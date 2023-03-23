package dependentChisel.tests

/* imperativeStyle dependent chisel */
import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScalaCross
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

import dependentChisel.codegen.compiler.*
import tests.ifTest.*

import algo.seqCmd2tree.*

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.codegen.firrtlTypes.FirrtlCircuit

object ifTest extends mainRunnable {
  // var globalDepInfo
// stdScalaCross.
  override def main(args: Array[String] = Array()): Unit = run

  def run = {
    // (1, 2, 3).mapConst((x: Int) => x * 2)
    val (mod, globalCircuit) = makeModule { implicit p =>
//   new IfElse1
      new IfModNested // ok
    }

    val fMod = chiselMod2firrtlCircuits(mod)
    val firCirc = firrtlCircuits2str(fMod)
    println(firCirc)
    firrtlUtils.firrtl2verilog(firCirc)

  }

  /* module contains nested if */
  class IfModNested(using parent: GlobalInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val c = newInput[16]("c")
    val y = newOutput[16]("y")

    dbg(y)
    y := a - b
    // val aa: n + 2 = n + 2
    If(a === b) {
      y := a + b
      If(a === c) {
        y := a * b
      }
    }
  }

  class IfElse1(using parent: GlobalInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val y = newOutput[16]("y")

    IfElse(a === b) {
      y := a + b
    } {
      y := a - b
    }
  }

  class IfMod(using parent: GlobalInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val y = newOutput[16]("y")
    val y2 = newOutput[16]("y2")

    y := a - b
    /* should require initialized y2:=0
within firrtl or before firrtl
maybe static analysis
     */
    If(a === b) {
      y := a + b
      y2 := a - b

    }
  }

}

/*
module IfNested :
  input clock : Clock
  input reset : UInt<1>
  output io : { flip a : UInt<16>, flip b : UInt<16>, flip c : UInt<16>, y : UInt<16>}

  node _io_y_T = sub(io.a, io.b) @[IfNested.scala 26:16]
  node _io_y_T_1 = tail(_io_y_T, 1) @[IfNested.scala 26:16]
  io.y <= _io_y_T_1 @[IfNested.scala 26:8]
  node _T = eq(io.a, io.b) @[IfNested.scala 27:13]
  when _T : @[IfNested.scala 27:23]
    node _io_y_T_2 = add(io.a, io.b) @[IfNested.scala 28:18]
    node _io_y_T_3 = tail(_io_y_T_2, 1) @[IfNested.scala 28:18]
    io.y <= _io_y_T_3 @[IfNested.scala 28:10]
    node _T_1 = eq(io.a, io.c) @[IfNested.scala 29:15]
    when _T_1 : @[IfNested.scala 29:25]
      node _io_y_T_4 = mul(io.a, io.b) @[IfNested.scala 30:20]
      io.y <= _io_y_T_4 @[IfNested.scala 30:12]
 */
