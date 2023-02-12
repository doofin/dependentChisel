package dependentChisel.syntax

import scala.compiletime.ops.int.*
import scala.compiletime.*

import com.doofin.stdScalaJvm.*

import dependentChisel.syntax.dslAST.NewWire
import dependentChisel.macros.getTypeTerm
import dependentChisel.macros.getTypeInfo
import dependentChisel.macros

object bundles {
//similar to modules in chisel
  trait Mod1[bund, circ] {
    def createBundle: bund
    def createCircuit: circ
  }

// chisel type
  trait MyBundle[A <: MyBundle[_]] {
    def getTp = {} // getTypeInfo[A]
  }

  trait MyModule[A <: MyModule[_]] {
    def getTp = {} // getTypeInfo[A]
  }

  case class UIntDep[a <: Int]() {
    // def +[b <: Int](oth: Uint[b]) = { Uint[a + b]() }
    def +(oth: UIntDep[a]) = { UIntDep[a]() }
    def :=(oth: UIntDep[a]) = { (this, oth, "+") }
    def asInput = this
    def asOutput = this
    inline def valu = constValueOpt[a]
  }

//correct, in dep chisel (this project)

  case class MyAdderBundle1() extends MyBundle[MyAdderBundle1] {
    val a = UIntDep[8]().asInput
    val b = UIntDep[8]().asInput
    val y = UIntDep[8]().asOutput
  }

  case class MyAdder1() extends MyModule[MyAdder1] {
    val bundle1 = MyAdderBundle1()
    bundle1.y := bundle1.a + bundle1.b
  }

  val adder1 = MyAdderBundle1()
  // adder1.toFir
  // macros.inspect(adder1)
  // macros.getTypeTerm(adder1)
  // macros.getTypeInfo[MyAdderBundle1]

  /* use macros to insert type into bundle from case class def?
  https://spinalhdl.github.io/SpinalDoc-RTD/master/SpinalHDL/Data%20types/bundle.html
   */

}

/* object UIntDep {
    // def apply[t <: Int]: UintDep[t] = UintDep[t]()
  } */
