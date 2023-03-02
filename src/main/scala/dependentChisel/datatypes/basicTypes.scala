package dependentChisel.datatypes
import dependentChisel.*

import scala.compiletime.ops.int.*
import scala.compiletime.*

import com.doofin.stdScalaJvm.*
import syntax.ImperativeModules.*
import dependentChisel.macros.getVarName
import syntax.tree.*
import depTypes.*

object basicTypes {
// https://github.com/MaximeKjaer/tf-dotty/blob/master/modules/compiletime/src/main/scala/io/kjaer/compiletime/Shape.scala
  type Shape[width <: Int]
  /* Chisel provides three data types to describe connections, combinational logic, and
registers: Bits, UInt, and SInt. UInt and SInt extend Bits, and all three types
represent a vector of bits */
  enum BitsType[idx <: Int] {
    case UInt() extends BitsType[0]
    case SInt() extends BitsType[1]
    case Bits() extends BitsType[2]
    // inline def valu = constValueOpt[width]
  }

  case class BinOp[w <: Int](a: Expr[w], b: Expr[w], nm: String)
      extends Expr[w] {
    override def toString(): String = s"${a} ${nm} ${b}"
  }

  /* Combinational Circuits */

  trait Expr[w <: Int] {
    def +(oth: Expr[w]) = BinOp(this, oth, "+")
    def -(oth: Expr[w]) = BinOp(this, oth, "-")
    def |(oth: Expr[w]) = BinOp(this, oth, "|")
    def &(oth: Expr[w]) = BinOp(this, oth, "&")
    def ===(oth: Expr[w]) = Bool(this, oth)
  }

  /* more elaborate expr with bits type */
  // type sp[w,b]=(w <: Int, b <: BitsType[w])
  trait ExprB[w <: Int, b <: BitsType[w]] {
    def +(oth: ExprB[w, b]) = { "ok!!" }
    // def ab(oth: BitsType[w]) = {}
  }

  case class Bool[w <: Int](a: Expr[w], b: Expr[w]) extends Expr[1] {
    override def toString(): String = s"${a} == ${b}"
  }

}
