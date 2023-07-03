package dependentChisel.tests

import scala.compiletime.*
import scala.compiletime.ops.int.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.*
import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.varDecls.*
import dependentChisel.typesAndSyntax.chiselModules.*

import dependentChisel.codegen.compiler.*

/* static parametric modules */
object parametric extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val mod = makeModule { implicit p => new AdderComb4TypeParm }

    chiselMod2verilog(mod)
  }

  class AdderParm[I <: Int: ValueOf](using GlobalInfo) extends UserModule {
    val a = newInput[I]("a")
    val b = newInput[I]("b")
    val y = newOutput[I]("y")

    y := a + b
  }

  class AdderComb4TypeParm(using parent: GlobalInfo) extends UserModule {
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

// works if with inline
  inline def adderTypeParam2[I <: Int: ValueOf](using mli: ModLocalInfo) = {
    val a = newIO[I](VarType.Input)
    val b = newIO[I](VarType.Input)
    val y = newIO[I](VarType.Output)
    y := a + b
  }

  class AdderTypeParmNotWork2(val size: Int)(using parent: GlobalInfo)
      extends UserModule {

    val a = newInput[size.type]("a") // val a = newInput[size.type]("a",size)
    val b = newInput[size.type]("b")
    val y = newOutput[size.type]("y")

    // m1.y := a - b // will both err
    y := a + b
  }

}

/*
/* compromise : in order to make it work by inline def and trait interface  */
  /* old way module call */
  trait Adder1I[I <: Int] {
    val a: VarTyped[I]
    val b: VarTyped[I]
    val y: VarTyped[I]
  }

inline def adder1TypeParamMod[I <: Int: ValueOf](using GlobalInfo, ModLocalInfo) =
    new UserModule with Adder1I[I] {
      val a = newIO[I](VarType.Input)
      val b = newIO[I](VarType.Input)
      val y = newIO[I](VarType.Output)
      y := a - b
    }

class AdderComb4TypeParamMod(using GlobalInfo) extends UserModule {
    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val c = newInput[2]("c")
    val d = newInput[2]("d")
    val y = newOutput[2]("y")

    val m1 = newMod(adder1TypeParamMod[2])
    val m2 = newMod(adder1TypeParamMod[2])

    m1.a := a
    m1.b := b
    m2.a := c
    m2.b := d

    y := m1.y + m2.y
  }
 */
