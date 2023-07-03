package dependentChisel.tests

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
      new AdderMixed(2)
    }

    chiselMod2verilog(mod)
  }

  class Adder1(using GlobalInfo) extends UserModule {
    val a = newIO[2](VarType.Input)
    val b = newIO[2](VarType.Input)
    val y = newIO[2](VarType.Output)

    y := a + b
  }

  /* only works if size=2 */
  class AdderMixed(using GlobalInfo)(size: Int) extends UserModule {
    val a = newIO[2](VarType.Input)
    val b = newIO[2](VarType.Input)
    val y = newIODym(size, VarType.Output)

    y := a + b
  }

  class AdderCall1(using parent: GlobalInfo) extends UserModule {

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    // val c = newInput[2]("c")
    // val d = newInput[2]("d")
    val y = newOutput[2]("y")

    val m1 = newMod(new Adder1) // might be able to rm this
    // m1.y := a - b // will both err
    m1.a := a
    m1.b := b
    y := m1.y
  }

  class AdderComb4(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val c = newInput[2]("c")
    val d = newInput[2]("d")
    val y = newOutput[2]("y")

    val m1 = newMod(new Adder1) // might be able to rm this
    val m2 = newMod(new Adder1) // might be able to rm this
    // m1.y := a - b // will both err
    m1.a := a
    m1.b := b
    m2.a := c
    m2.b := d

    y := m1.y + m2.y
  }

}

/*
// not work
  class DoubleAdder3(val size: Int)(using parent: GlobalInfo) extends UserModule {

    val a = newInput[size.type]("a") // val a = newInput[size.type]("a",size)
    val b = newInput[size.type]("b")
    // val s2 = ""
    // newInput[s2.type]("b")
    // val c = newInput[2]("c")
    // val d = newInput[2]("d")
    val y = newOutput[size.type]("y")
    // val m1 = newMod(new Adder1) // might be able to rm this
    // m1.y := a - b // will both err
    y := a + b
  }

 */
