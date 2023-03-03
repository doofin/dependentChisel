package dependentChisel.typesAndSyntax
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
  trait Var[w <: Int](name: String) {
    def getname = name
    inline def getLit(i: Bounded[0, w]) = { toLit[w](i) }
    // def getLit = LitFromVar(1, this)

    /* inline def :=(using ModCircuits)(oth: Expr[w]) = {
      summon.commands += s"${getVarName(this)} := ${oth},width=${constValueOpt[w]}"
    } */
  }
  // add(BitsType.UInt(), BitsType.SInt()) // fail,ok
  def add[idx <: Int, b <: BitsType[idx]](x: b, y: b) = {}

  case class BinOp[w <: Int](a: Expr[w], b: Expr[w], nm: String)
      extends Expr[w] {
    override def toString(): String = s"${a} ${nm} ${b}"
  }

  case class Bool[w <: Int](a: Expr[w], b: Expr[w]) extends Expr[1] {
    override def toString(): String = s"${a} == ${b}"
  }
  trait Expr[w <: Int] {
    def +(oth: Expr[w]) = BinOp(this, oth, "+")
    def *(oth: Expr[w]) = BinOp(this, oth, "+")
    def -(oth: Expr[w]) = BinOp(this, oth, "-")
    def |(oth: Expr[w]) = BinOp(this, oth, "|")
    def &(oth: Expr[w]) = BinOp(this, oth, "&")
    def ===(oth: Expr[w]) = Bool(this, oth)
  }
  trait ExprB[w <: Int, b <: BitsType[w]] {
    def +(oth: ExprB[w, b]) = { "ok!!" }
  }

  // case class Lit[w <: Int](i: Int) extends Expr[w] {}
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

}
