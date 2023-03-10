package dependentChisel.typesAndSyntax

import scala.compiletime.ops.int.*
import scala.compiletime.*

import dependentChisel.*

import com.doofin.stdScalaJvm.*
import syntax.ImperativeModules.*
import dependentChisel.macros.getVarName
import syntax.tree.*
// import depTypes.*

object basicTypes {
// https://github.com/MaximeKjaer/tf-dotty/blob/master/modules/compiletime/src/main/scala/io/kjaer/compiletime/Shape.scala
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

  /* mutable vars in lhs */
  sealed trait Var[w <: Int](name: String) {
    def getname = name
  }

  case class VarLit[w <: Int](name: String) extends Expr[w] with Var[w](name)
  // add(BitsType.UInt(), BitsType.SInt()) // fail,ok
  def add[idx <: Int, b <: BitsType[idx]](x: b, y: b) = {}

  sealed trait Expr[w <: Int]
  case class BinOp[w <: Int](a: Expr[w], b: Expr[w], nm: String) extends Expr[w]

  /* case class Bool[w <: Int](a: Expr[w], b: Expr[w]) extends Expr[w] {
    override def toString(): String = s"${a} == ${b}"
  } */

  case class Bool[w <: Int]() extends Expr[w]

  extension [w <: Int](x: Expr[w]) {
    def +(oth: Expr[w]) = BinOp(x, oth, "+")
    def *(oth: Expr[w]) = BinOp(x, oth, "*")
    def -(oth: Expr[w]) = BinOp(x, oth, "-")
    def |(oth: Expr[w]) = BinOp(x, oth, "|")
    def &(oth: Expr[w]) = BinOp(x, oth, "&")
    // def ===(oth: Expr[w]) = Bool(x, oth)
    def ===(oth: Expr[w]) = Bool()
  }

  case class Input[w <: Int](name: String) extends Var[w](name), Expr[w] {}

  case class Output[w <: Int](name: String = "")(using ModLocalInfo)
      extends Var[w](name),
        Expr[w] {}
  /* sealed trait Expr[w <: Int] {
    def +(oth: Expr[w]) = BinOp(this, oth, "+")
    def *(oth: Expr[w]) = BinOp(this, oth, "*")
    def -(oth: Expr[w]) = BinOp(this, oth, "-")
    def |(oth: Expr[w]) = BinOp(this, oth, "|")
    def &(oth: Expr[w]) = BinOp(this, oth, "&")
    def ===(oth: Expr[w]) = Bool(this, oth)
  } */

  // future
  trait ExprB[w <: Int, b <: BitsType[w]] {
    def +(oth: ExprB[w, b]) = { "ok!!" }
  }
  case class Lit[w <: Int](i: w) extends Expr[w] {}
  /*
  case class Lit[w <: Int](i: Bounded[0, w]) extends Expr[w] {}
  // Lit[5](1)

  case class Lit2[w <: Int](i: Bounded[0, w * 2]) extends Expr[w] {}
  Lit2[1](2)

  inline def toLit[w <: Int](i: Bounded[0, w]) = {
    // val b2: iw < w = true
    Lit[w](i)
  }

  toLit[5](1)

  inline def LitFromVar[w <: Int](i: Bounded[0, w], v: Var[w]) = {
    // val b2: iw < w = true
    Lit[w](i)
  }

  extension [w <: Int](i: Bounded[0, w]) {
    def asLit = toLit[w](i)
  }
   */
}
