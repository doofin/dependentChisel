package dependentChisel.typesAndSyntax

import scala.compiletime.ops.int.*
import scala.compiletime.*

import com.doofin.stdScalaJvm.*

import dependentChisel.*
import dependentChisel.macros.getVarName
// import dependentChisel.typesAndSyntax.chiselModules.*

/*
https://github.com/MaximeKjaer/tf-dotty/blob/master/modules/compiletime/src/main/scala/io/kjaer/compiletime/Shape.scala
https://github.com/MaximeKjaer/tf-dotty/blob/master/modules/compiletime/src/main/scala/io/kjaer/compiletime/Shape.scala
 */
object basicTypes {

  /* Chisel provides three data types to describe connections, combinational logic, and
registers: Bits, UInt, and SInt. UInt and SInt extend Bits, and all three types
represent a vector of bits */

  /** might not need,only uint appear in serial port */
  enum BitsType[bitsTp <: Int, width <: Int](v: Int) {
    case UInt[width <: Int](v: Int) extends BitsType[0, width](v)
    case SInt[width <: Int](v: Int) extends BitsType[1, width](v)
    case Bits[width <: Int](v: Int) extends BitsType[2, width](v)
    // inline def valu = constValueOpt[width]
  }
  def add[idx <: Int, width <: Int, b <: BitsType[idx, width]](
      x: b,
      y: b
  ) = {}
  add(BitsType.UInt[2](1), BitsType.UInt[2](2))

  /* mutable vars in lhs */
  sealed trait Var[w <: Int](name: String, isIO: Boolean)
      extends Expr[w],
        BoolEx[w] {
    def getname = name
    def getIsIO = isIO
  }

  case class VarLit[w <: Int](name: String) extends Var[w](name, false)

  sealed trait Expr[w <: Int]
  case class BinOp[w <: Int](a: Expr[w], b: Expr[w], nm: String) extends Expr[w]
  sealed trait BoolEx[w <: Int] extends Expr[w]
  case class BoolExpr[w <: Int](expr: Expr[w]) extends BoolEx[w]

  extension [w <: Int](x: Expr[w]) {
    def +(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "+")
    def *(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "*")
    def -(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "-")
    def |(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "|")
    def &(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "&")
    // def ===(oth: Expr[w]) = Bool(x, oth)
    def ===(oth: Expr[w]): BoolExpr[w] = BoolExpr(BinOp(x, oth, "=="))
  }

  case class Input[w <: Int](name: String) extends Var[w](name, true)

  case class Output[w <: Int](name: String = "") extends Var[w](name, true)
  /* sealed trait Expr[w <: Int] {
    def +(oth: Expr[w]) = BinOp(this, oth, "+")
    def *(oth: Expr[w]) = BinOp(this, oth, "*")
    def -(oth: Expr[w]) = BinOp(this, oth, "-")
    def |(oth: Expr[w]) = BinOp(this, oth, "|")
    def &(oth: Expr[w]) = BinOp(this, oth, "&")
    def ===(oth: Expr[w]) = Bool(this, oth)
  } */

  // future
  trait ExprB[idx <: Int, width <: Int, b <: BitsType[idx, width]] {
    def +(oth: ExprB[idx, width, b]) = { "ok!!" }
  }

  case class Lit[w <: Int](i: w) extends Expr[w] {}
  /*
  extension [w <: Int](i: Bounded[0, w]) {
    def asLit = toLit[w](i)
  }
   */
}
