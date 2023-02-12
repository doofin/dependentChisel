package dependentChisel

import scala.compiletime.ops.int.*
import scala.compiletime.*

import com.doofin.stdScalaJvm.*
import syntax.imperative.*
object dataTypes {
// https://github.com/MaximeKjaer/tf-dotty/blob/master/modules/compiletime/src/main/scala/io/kjaer/compiletime/Shape.scala
  type Shape[width <: Int]
  /* Chisel provides three data types to describe connections, combinational logic, and
registers: Bits, UInt, and SInt. UInt and SInt extend Bits, and all three types
represent a vector of bits */
  enum BitsType[width <: Int] {
    case UInt()
    case SInt()
    case Bits()
    inline def valu = constValueOpt[width]
  }

  enum BitsType2[width <: Int] {
    case UInt extends BitsType2[1]
    case SInt extends BitsType2[2]
  }
  def add[width <: Int, b <: BitsType2[width]](x: b, y: b) = {}

  // add(BitsType2.UInt, BitsType2.SInt)

  enum BitsType3[t <: BitsType3[t]] {
    case UInt()
    case SInt()
  }

  def add3[b <: BitsType3[?]](x: b, y: b) = {}

  add3(BitsType3.UInt(), BitsType3.SInt())

  trait Bits[w <: Int]
  // trait Out
  // trait In

  trait Expr[w <: Int] {
    def +(oth: Expr[w]) = BinOp(this, oth, "+")
    def ab(oth: BitsType[w]) = {}
  }
  // type sp[w,b]=(w <: Int, b <: BitsType[w])
  trait ExprB[w <: Int, b <: BitsType[w]] {
    def +(oth: ExprB[w, b]) = { "ok!!" }
    // def ab(oth: BitsType[w]) = {}
  }
  InputB(BitsType.Bits[1]()) + InputB(BitsType.UInt[1]())

  case class BinOp[w <: Int](a: Expr[w], b: Expr[w], nm: String) extends Expr[w] {
    override def toString(): String = s"${a} ${nm} ${b}"
  }

  trait Var[w <: Int] {
    inline def :=(using ModCircuits)(oth: Expr[w]) = {
      summon.commands += s"${this} := ${oth},width=${constValueOpt[w]}"
    }
  }

  /** w:width */
  case class UIntDep[w <: Int]() extends Bits[w] {
    inline def valu = constValueOpt[w]
  }

  case class Input[w <: Int]()(using ModCircuits) extends Expr[w] {
    // summon.io.prepend(this.toString() + ":" + constValueOpt[w])
  }

  inline def newInput[w <: Int](using ModCircuits) = {
    val r = Input[w]()
    summon.io.prepend(r.toString() + ":" + constValueOpt[w])
    r
  }

  case class InputB[w <: Int, b <: BitsType[w]](x: b) extends ExprB[w, b] {}

  case class Output[w <: Int]()(using ModCircuits) extends Var[w] {}

  inline def newOutput[w <: Int](using ModCircuits) = {
    val r = Output[w]()
    summon.io.prepend(r.toString() + ":" + constValueOpt[w])
    r
  }

  // def add[t <: Int](x: t, y: t) = {}
  // add(1, 2)
  /* Combinational Circuits */

}
