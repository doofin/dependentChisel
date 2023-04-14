package dependentChisel.typesAndSyntax

import scala.compiletime.ops.int.*
import scala.compiletime.*

import com.doofin.stdScalaJvm.*

import dependentChisel.*
import dependentChisel.macros.getVarName
// import dependentChisel.typesAndSyntax.chiselModules.*

/*
https://github.com/MaximeKjaer/tf-dotty/blob/master/modules/compiletime/src/main/scala/io/kjaer/compiletime/Shape.scala
 */
object typesAndOps extends exprOperators {

  /* Chisel provides three data types to describe connections, combinational logic, and
registers: Bits, UInt, and SInt. UInt and SInt extend Bits, and all three types
represent a vector of bits */

  /* mutable vars which can be mutated in lhs, incl input,output */
  sealed trait Var[w <: Int](name: String) extends Expr[w] {
    def getname = name
    // def getIsIO = isIO
  }

  case class VarLit[w <: Int](name: String) extends Var[w](name)

  sealed trait Expr[w <: Int]
  case class BinOp[w <: Int](a: Expr[w], b: Expr[w], nm: String) extends Expr[w]

  // sealed trait BoolEx[w <: Int] extends Expr[w]
  case class BoolExpr[w <: Int](expr: Expr[w]) extends Expr[w]

  /** Wire, Reg, and IO */
  enum VarDeclTp { case Input, Output, Reg, Wire }

  /** untyped API for Wire, Reg, and IO */
  case class VarDymTyped(width: Int, tp: VarDeclTp, name: String)
      extends Var[Nothing](name)

  case class Input[w <: Int](name: String) extends Var[w](name)

  // case class Input[w <: Int](name: String) extends Var[w](name, true)

  case class Output[w <: Int](name: String) extends Var[w](name)

  
  // for future use

  case class Lit[w <: Int](i: w) extends Expr[w] {}
  /*
  extension [w <: Int](i: Bounded[0, w]) {
    def asLit = toLit[w](i)
  }
   */
}
