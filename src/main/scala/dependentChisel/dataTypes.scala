package dependentChisel

import scala.compiletime.ops.int.*
import scala.compiletime.*

import com.doofin.stdScalaJvm.*
import syntax.ImperativeModules.*
import dependentChisel.macros.getVarName
object dataTypes {
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
  def add[idx <: Int, b <: BitsType[idx]](x: b, y: b) = {}
  // add(BitsType.UInt(), BitsType.SInt()) // fail,ok

  trait Expr[w <: Int] {
    def +(oth: Expr[w]) = BinOp(this, oth, "+")
    def ab(oth: BitsType[w]) = {}
  }
  // type sp[w,b]=(w <: Int, b <: BitsType[w])
  trait ExprB[w <: Int, b <: BitsType[w]] {
    def +(oth: ExprB[w, b]) = { "ok!!" }
    // def ab(oth: BitsType[w]) = {}
  }
  // InputB(BitsType.Bits()) + InputB(BitsType.UInt()) // fail,ok
  InputB(BitsType.Bits()) + InputB(BitsType.Bits())

  case class BinOp[w <: Int](a: Expr[w], b: Expr[w], nm: String) extends Expr[w] {
    override def toString(): String = s"${a} ${nm} ${b}"
  }

  /* inline def connect[w <: Int](using ModCircuits)(a: Expr[w], b: Expr[w]) = {
    summon.commands += s"${getVarName(a)} := ${b},width=${constValueOpt[w]}"
  } */

  extension [w <: Int, modTp <: Var[w]](v: modTp) {

    inline def :=(using ModCircuits)(oth: Expr[w]) = {
      val parentClass = getVarName(v).split('.').tail.tail.mkString(".")
      val name =
        v.getname + "." + parentClass // summon.classNm + "." + getVarName(v).split('.').last // UserMod2.this.m1.y
      println(parentClass) // m1.y
      summon.commands += s"${name} := ${oth},width=${constValueOpt[w]}"
    }

    inline def :==(oth: Expr[w]) = {
      val parentClass = getVarName(v).split('.').tail.tail.mkString(".")
      val name =
        v.getname + "." + parentClass // summon.classNm + "." + getVarName(v).split('.').last // UserMod2.this.m1.y
      println(parentClass) // m1.y
    }

  }

  trait Var[w <: Int](name: String) {
    def getname = name
    /* inline def :=(using ModCircuits)(oth: Expr[w]) = {
      summon.commands += s"${getVarName(this)} := ${oth},width=${constValueOpt[w]}"
    } */
  }

  /** w:width */
  /* case class UIntDep[w <: Int]() extends Bits[w] {
    inline def valu = constValueOpt[w]
  } */

  case class Input[w <: Int](name: String = "") extends Var[w](name), Expr[w] {}
  case class InputB[w <: Int, b <: BitsType[w]](x: b) extends ExprB[w, b] {}

  inline def newInput[w <: Int](name: String = "")(using ModCircuits) = {
    val r = Input[w](summon.classNm + "." + name)
    dbg(r)
    summon[ModCircuits].io.prepend(s"input ${name}[${constValueOpt[w]}]")
    r
  }

  case class Output[w <: Int](name: String = "")(using ModCircuits) extends Var[w](name), Expr[w] {}

  inline def newOutput[w <: Int](name: String = "")(using ModCircuits) = {
    val r = Output[w](summon.classNm + "." + name)
    summon.io.prepend(s"output ${name}[${constValueOpt[w]}]")
    r
  }

  // def add[t <: Int](x: t, y: t) = {}
  // add(1, 2)
  /* Combinational Circuits */

}
