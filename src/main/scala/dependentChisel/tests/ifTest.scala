package dependentChisel.tests

/* imperativeStyle dependent chisel */
import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScalaCross
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
object ifTest extends mainRunnable {
  override def main(args: Array[String] = Array()): Unit = run

  def run = {
    // (1, 2, 3).mapConst((x: Int) => x * 2)
    val (mod, globalCircuit) = makeModule { implicit p =>
      // new IfElse1
      // new IfModNested
      // new IfModDangling // ok
      new IfMod
    }
    // ppc(mod.modLocalInfo.commands)
    // pp(mod.modLocalInfo.typeMap)
    val fMod = chiselMod2firrtlCircuits(mod)
    pp(fMod.modules.map(_.ast))
    val firCirc = firrtlCircuits2str(fMod)
    println(firCirc)
    // firrtlUtils.firrtl2verilog(firCirc)

  }

  /* ok,module contains nested if */
  class IfModNested(using parent: GlobalInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val c = newInput[16]("c")
    val y = newOutput[16]("y")

    y := a - b
    // val aa: n + 2 = n + 2
    val cond1 = a === b
    val cond2 = (a === b) | (a === b)
    If(cond2) { // (a === b) | (a === b)
      y := a + b
      If(a === c) {
        y := a * b
      }
    }
  }

  /* failed */
  class IfElse1(using parent: GlobalInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val y = newOutput[16]("y")
// 5
    // y := a + b
// 0
// val newReg=...
    IfElse(a === b) {
      y := a + b
      // 1
    } {
      // 2
      y := a - b
    }
  }

  /* should require initialized y2:=0
within firrtl or before firrtl
maybe static analysis
   */
  class IfMod(using parent: GlobalInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val y = newOutput[16]("y")
    val y2 = newOutput[16]("y2")

    y := a - b
    If(a === b) {
      y := a + b
      y2 := a - b

    }
  }

// ok,will give err in firrtl compiler : Reference io is not fully initialized.
  class IfModDangling(using parent: GlobalInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val y = newOutput[16]("y")

    If(a === b) {
      y := a + b
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
