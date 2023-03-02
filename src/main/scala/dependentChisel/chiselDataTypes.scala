package dependentChisel

import scala.compiletime.ops.int.*
import scala.compiletime.*

import dependentChisel.*
import com.doofin.stdScalaJvm.*
import syntax.ImperativeModules.*
import dependentChisel.macros.getVarName
import syntax.tree.*
import depTypes.*

object chiselDataTypes {
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

  // add(BitsType.UInt(), BitsType.SInt()) // fail,ok

  trait Expr[w <: Int] {
    def +(oth: Expr[w]) = BinOp(this, oth, "+")
    def -(oth: Expr[w]) = BinOp(this, oth, "-")
    def |(oth: Expr[w]) = BinOp(this, oth, "|")
    def &(oth: Expr[w]) = BinOp(this, oth, "&")
    def ===(oth: Expr[w]) = Bool(this, oth)
  }
  // type sp[w,b]=(w <: Int, b <: BitsType[w])
  trait ExprB[w <: Int, b <: BitsType[w]] {
    def +(oth: ExprB[w, b]) = { "ok!!" }
    // def ab(oth: BitsType[w]) = {}
  }

  enum Stmt {
    case StmtNoOp
  }

  // InputB(BitsType.Bits()) + InputB(BitsType.UInt()) // fail,ok
  // InputB(BitsType.Bits()) + InputB(BitsType.Bits())

  def add[idx <: Int, b <: BitsType[idx]](x: b, y: b) = {}

  case class BinOp[w <: Int](a: Expr[w], b: Expr[w], nm: String)
      extends Expr[w] {
    override def toString(): String = s"${a} ${nm} ${b}"
  }

  case class Bool[w <: Int](a: Expr[w], b: Expr[w]) extends Expr[1] {
    override def toString(): String = s"${a} == ${b}"
  }
  /* inline def connect[w <: Int](using ModCircuits)(a: Expr[w], b: Expr[w]) = {
    summon.commands += s"${getVarName(a)} := ${b},width=${constValueOpt[w]}"
  } */

  extension [w <: Int, modTp <: Var[w]](v: modTp) {

    inline def :=(using BlockLocalInfo)(oth: Expr[w]) = {
      // val parentClass = getVarName(v).split('.').tail.tail.mkString(".")
      // val name =
      //   v.getname + "." + parentClass // summon.classNm + "." + getVarName(v).split('.').last // UserMod2.this.m1.y
      // println(parentClass) // m1.y
      val name = v.getname
      summon += s"${name} := ${oth},width=${constValueOpt[w]}"
      Stmt.StmtNoOp
    }

    inline def :==(using ModLocalInfo)(oth: Expr[w]) = {
      // val parentClass = getVarName(v).split('.').tail.tail.mkString(".")
      // val name =
      //   v.getname + "." + parentClass // summon.classNm + "." + getVarName(v).split('.').last // UserMod2.this.m1.y

      // println(parentClass) // m1.y
      val name = v.getname
      summon.commands += s"${name} := ${oth},width=${constValueOpt[w]}"
      Stmt.StmtNoOp
    }

  }

  trait Var[w <: Int](name: String) {
    def getname = name
    inline def getLit(i: Bounded[0, w]) = { toLit[w](i) }
    // def getLit = LitFromVar(1, this)

    /* inline def :=(using ModCircuits)(oth: Expr[w]) = {
      summon.commands += s"${getVarName(this)} := ${oth},width=${constValueOpt[w]}"
    } */
  }
  // extension [w <: Int](v: Var[w]) {

  // }

  /** w:width */
  /* case class UIntDep[w <: Int]() extends Bits[w] {
    inline def valu = constValueOpt[w]
  } */

  case class Input[w <: Int](name: String) extends Var[w](name), Expr[w] {}

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

  // 1.asLit

  case class InputB[w <: Int, b <: BitsType[w]](x: b) extends ExprB[w, b] {}

  inline def newInput[w <: Int](using m: ModLocalInfo, dp: DependenciesInfo)(
      givenName: String = ""
  ) = {
    val name = s"${m.classNm}.$givenName${dp.counter.getIdWithDash}"
    m.io.prepend(s"input ${name}[${constValueOpt[w]}]")
    Input[w](name)
  }

// varW <: Int
  def switch[condW <: Int](using
      m: ModLocalInfo
  )(cond: Var[condW])(cases: (Expr[condW], Stmt)*) = {
    // nested stmt? exhaustive check?
    // m.io.prepend(s"input ${name}[${constValueOpt[w]}]")
    // case classs switch
    Stmt.StmtNoOp
  }

  def when[condW <: Int](using
      m: ModLocalInfo
  )(cond: Var[condW])(stmts: Stmt*) = {
    Stmt.StmtNoOp
  }
  case class Output[w <: Int](name: String = "")(using ModLocalInfo)
      extends Var[w](name),
        Expr[w] {}

  inline def newOutput[w <: Int](using m: ModLocalInfo, dp: DependenciesInfo)(
      givenName: String = ""
  ) = {
    val name = s"${m.classNm}.$givenName${dp.counter.getIdWithDash}"
    m.io.prepend(s"input ${name}[${constValueOpt[w]}]")
    Output[w](name)
  }

  // def add[t <: Int](x: t, y: t) = {}
  // add(1, 2)
  /* Combinational Circuits */

}
