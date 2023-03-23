import dependentChisel.*

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.codegen.compiler.*
import com.doofin.stdScalaCross.*
import tests.ifTest.*

import algo.seqCmd2tree.*
import dependentChisel.codegen.firrtlTypes.*
import dependentChisel.typesAndSyntax.chiselModules.*
/*
implicit def str2lit(s: String): VarLit[Nothing] = VarLit(s)
// ok
pp(toANF(FirStmt("y", ":=", Lit(1) + Lit(2))))
val r = toANF(FirStmt("y", ":=", Lit(1) + Lit(2) + Lit(3)))
pp(r)

// ok
val r2 = toANF(FirStmt("y", ":=", Lit(1) + Lit(2) + Lit(3) + Lit(4)))
pp(r2)
 */

// tests.ifTest.run

val (mod, depInfo) = makeModule { implicit p =>
//   new IfElse1
  new IfModNested // ok
}

val fMod = chiselMod2firrtlModule(mod)
val firrtlStr = firrtlModule2str(fMod)
val firCirc = firrtlCircuits2str(FirrtlCircuit(fMod.name, List(fMod)))
println(firCirc)

firrtlUtils.firrtl2verilog(firCirc)
println(firrtlStr)
// need to deal with io names
/* ok for IfModNested
    y := a - b
    If(a === b) {
      y := a + b
      If(a === c) {
        y := a * b
      }
    } */

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

// println(modIOstr)

firrtlOpMap.toList
