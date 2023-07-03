package dependentChisel.tests

import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

import dependentChisel.typesAndSyntax.chiselModules.*

import dependentChisel.typesAndSyntax.control
import dependentChisel.typesAndSyntax.varDecls.*
import dependentChisel.codegen.compiler.*

object gcd extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val mod = makeModule { implicit p => new gcdParam[16] }

    chiselMod2verilog(mod)
  }

  class gcdParam[I <: Int: ValueOf](using parent: GlobalInfo) extends UserModule {
    val value1 = newInput[I]()
    val value2 = newInput[I]()
    val outputGCD = newOutput[I]()

    val loadingValues = newInput[1]()
    val outputValid = newOutput[1]()

    val x = newReg[I]()
    val y = newReg[I]()

    IfElse(x > y) { x := x - y } { y := y - x }

    If(loadingValues) {
      x := value1
      y := value2
    }

    outputGCD := x
    outputValid := (y === newLitp[I](0)) // io.outputValid := y === 0.U

  }

}
