package dependentChisel.examples

import scala.compiletime.*
import scala.compiletime.ops.int.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.*
import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.typesAndSyntax.varDecls.newIO
import dependentChisel.codegen.compiler.*

// import dependentChisel.api.*

import dependentChisel.typesAndSyntax.varDecls.newIODym

object adder extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val mod = makeModule { implicit p =>
      new AdderComb4
      // new AdderMixed(2) // change to 1 for detect
    }

    chiselMod2verilog(mod)
  }

  /* static adder


   */
  class Adder1(using GlobalInfo) extends UserModule {
    val a = newIO[2](VarType.Input)
    val b = newIO[2](VarType.Input)
    val y = newIO[2](VarType.Output)

    y := a + b
  }

  /*
  parametric static adder







   */
  class AdderParm[I <: Int: ValueOf](using GlobalInfo) extends UserModule {
    val a = newInput[I]("a")
    val b = newInput[I]("b")
    val y = newOutput[I]("y")

    y := a + b
  }

  /*
  calling parametric static adder




   */
  /* adder with 4 inputs */
  class AdderComb4(using parent: GlobalInfo) extends UserModule {

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val c = newInput[2]("c")
    val d = newInput[2]("d")
    val y = newOutput[2]("y")

    val m1 = newMod(new AdderParm[2])
    val m2 = newMod(new AdderParm[2])

    m1.a := a
    m1.b := b
    m2.a := c
    m2.b := d

    y := m1.y + m2.y
  }

  /* mixed static and dynamic signals. only works if size=2






   */
  class AdderMixed(using GlobalInfo)(size: Int) extends UserModule {
    val a = newIO[2](VarType.Input)
    val b = newIO[2](VarType.Input)
    val y = newIODym(size, VarType.Output)

    y := a + b
  }

  /*






   */
  class AdderStaticCall(using parent: GlobalInfo) extends UserModule {

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    val m1 = newMod(new Adder1)
    m1.a := a
    m1.b := b
    y := m1.y
  }
}
