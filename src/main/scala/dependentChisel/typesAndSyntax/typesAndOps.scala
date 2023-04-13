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
object typesAndOps {

  /* Chisel provides three data types to describe connections, combinational logic, and
registers: Bits, UInt, and SInt. UInt and SInt extend Bits, and all three types
represent a vector of bits */

  // may only need UINT for serial port example(although it needs Bool type )
  enum bt2 { case u, s, b }

  trait ctype[width <: Int, b <: bt2]
  case class u1[width <: Int]() extends ctype[width, bt2.u.type]
  case class s1[width <: Int]() extends ctype[width, bt2.s.type]

  trait ExprB[idx <: bt2, width <: Int, b <: ctype[width, idx]] {
    // def +(oth: ExprB[idx, width, b]) = { "ok!!" }
  }

  def add2[idx <: bt2, width <: Int, b <: ctype[width, idx]](
      x: b,
      y: b
  ) = {}

  add2(u1[1](), u1[1]())

  /* mutable vars which can be mutated in lhs, incl input,output */
  sealed trait Var[w <: Int](name: String) extends Expr[w], BoolEx[w] {
    def getname = name
    // def getIsIO = isIO
  }

  case class VarLit[w <: Int](name: String) extends Var[w](name)

  sealed trait Expr[w <: Int]
  case class BinOp[w <: Int](a: Expr[w], b: Expr[w], nm: String) extends Expr[w]
  sealed trait BoolEx[w <: Int] extends Expr[w]
  case class BoolExpr[w <: Int](expr: Expr[w]) extends BoolEx[w]

  extension [w <: Int](x: Expr[w]) {
    def +(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "+")
    def -(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "-")
    def *(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "*")
    def |(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "|")
    def &(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "&")
    // def ===(oth: Expr[w]) = Bool(x, oth)
    def ===(oth: Expr[w]): BoolExpr[w] = BoolExpr(BinOp(x, oth, "=="))
  }

  case class Input[w <: Int](instName: String, name: String)
      extends Var[w](instName + "." + name)

  // case class Input[w <: Int](name: String) extends Var[w](name, true)

  case class Output[w <: Int](instName: String, name: String)
      extends Var[w](instName + "." + name)

  /** Wire, Reg, and IO */
  enum VarDeclTp { case Input, Output, Reg, Wire }

  /** untyped API for Wire, Reg, and IO */
  case class VarDymTyped(width: Int, tp: VarDeclTp, name: String = "")
      extends Var[Nothing](name)

  /* sealed trait Expr[w <: Int] {
    def +(oth: Expr[w]) = BinOp(this, oth, "+")
    def *(oth: Expr[w]) = BinOp(this, oth, "*")
    def -(oth: Expr[w]) = BinOp(this, oth, "-")
    def |(oth: Expr[w]) = BinOp(this, oth, "|")
    def &(oth: Expr[w]) = BinOp(this, oth, "&")
    def ===(oth: Expr[w]) = Bool(this, oth)
  } */

  // for future use

  case class Lit[w <: Int](i: w) extends Expr[w] {}
  /*
  extension [w <: Int](i: Bounded[0, w]) {
    def asLit = toLit[w](i)
  }
   */
}
