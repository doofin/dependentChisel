package dependentChisel.syntax

import scala.compiletime.ops.int.*
import scala.compiletime.*

import com.doofin.stdScalaJvm.*

import dependentChisel.syntax.monadicAST.NewWire
import dependentChisel.macros.inspect
import dependentChisel.macros.getTypeTerm
import dependentChisel.macros.getTypeInfo

import dependentChisel.syntax.monadicAST
object bundlesAsTuple {
//similar to modules in chisel
  trait Mod1[bund, circ] {
    def createBundle: bund
    def createCircuit: circ
  }

  case class UintDep[a <: Int]() {
    // def +[b <: Int](oth: Uint[b]) = { Uint[a + b]() }
    def +(oth: UintDep[a]) = { UintDep[a]() }
    def :=(oth: UintDep[a]) = {}
    def asInput = this
    def asOutput = this
    inline def valu = constValueOpt[a]
  }

  object adder {
    case class bundleType[t](t: t) // { def c: t = { t } }

    type bund1Tp = bundleType[((String, UintDep[32]), (String, UintDep[32]))]
    inline def bund1 = bundleType(
      ("a" -> UintDep[32](), "b" -> UintDep[32]())
    )
  }

  extension [a <: Int](x: (String, UintDep[a])) {
    def +(y: (String, UintDep[a])) = { x._2 + y._2 }
  }

  import adder.*
  case class adder() extends Mod1[adder.bund1Tp, Unit] {
    def createBundle = {
      bund1
    }
    def createCircuit = {
// do operations like +,etc
      val (a, b) = bund1.t
      a + b
    }
    def toFir = {
      // val l = bund1.t.toList.map { case x: (String, uint[_]) =>
      //   x._1 + "," + x._2.valu
      // }
      bund1
      val l2 = bund1.t.map[[x] =>> String]([t] => (x: t) => x.toString())
      val l = bund1.t.map[[x] =>> String]([t] => (x: t) => x.toString())
      // map won't work since it needs natural trans like option
      l
    }
  }
  /*   val adder1 = adder()
  val bd1 = adder1.createBundle
  adder1.toFir
  import dependentChisel.macros
  macros.inspect(bd1)
  macros.getTypeTerm(bd1) */

  /* use macros to insert type into bundle from case class def?
  https://spinalhdl.github.io/SpinalDoc-RTD/master/SpinalHDL/Data%20types/bundle.html
   */

}
