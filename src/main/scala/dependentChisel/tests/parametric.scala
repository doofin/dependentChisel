package dependentChisel.tests

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.*
import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.varDecls.*

import dependentChisel.codegen.compiler.*
import dependentChisel.typesAndSyntax.chiselModules.*

import scala.compiletime.*
import scala.compiletime.ops.int.*

/* static parametric modules */
object parametric extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val (mod, depInfo: GlobalInfo) = makeModule { implicit p =>
      // new AdderTypeParmCallMod
      // new AdderTypeParmCallInline
      // new AdderComb4TypeParamMod // ok
      // val r = new AdderTypeParmNotWork[2]; println("r.i:" + r.i); r
      new AdderTypeParmNotWork[2]
    }
    val fMod = chiselMod2firrtlCircuits(mod)
    // pp(fMod.modules map (_.modInfo))
    val firCirc = firrtlCircuits2str(fMod)
    println(firCirc)

    val verilog = firrtlUtils.firrtl2verilog(firCirc)
    // println(verilog)
  }
  // type ConstInt=[I]=>>[I <: Int: ValueOf]
// now works ValueOf.  not work previously
  class AdderTypeParmNotWork[I <: Int: ValueOf](using GlobalInfo) extends UserModule {
    // inline def i = constValueOpt[I]
    // println("AdderTypeParmNotWork:" + valueOf[I])
    val a = newInput[I]("a")
    val b = newInput[I]("b")
    val y = newOutput[I]("y")

    // println(constValueOpt[I]) // is none

    y := a + b
  }

  /* compromise : in order to make it work by inline def and trait interface  */

  /*only need to define fields that will be used */
  trait adder[I <: Int] {
    val a: VarTyped[I]
    val b: VarTyped[I]
    // val y: VarTyped[I]
  }

  inline def adderTypeParamMod[I <: Int: ValueOf](using parent: GlobalInfo)(using
      mli: ModLocalInfo
  ) = new UserModule with adder[I] {
    val a = newIO[I](VarType.Input)
    val b = newIO[I](VarType.Input)
    // val y = newIO[I](VarType.Output)
    // y := a + b
  }

// works if with inline
  inline def adderTypeParam2[I <: Int: ValueOf](using mli: ModLocalInfo) = {
    val a = newIO[I](VarType.Input)
    val b = newIO[I](VarType.Input)
    val y = newIO[I](VarType.Output)
    y := a + b
  }

  /** works with inline methods */
  class AdderTypeParmCallMod(using parent: GlobalInfo) extends UserModule {
    val y = newIO[10](VarType.Output)
    val ad1 = newMod(adderTypeParamMod[10])
    // ad1.y := ad1.a + ad1.b // err : used as a SinkFlow but can only be used as a SourceFlow
    y := ad1.a + ad1.b
  }

  /** works with inline methods */
  class AdderTypeParmCallInline(using parent: GlobalInfo) extends UserModule {
    adderTypeParamMod[10]
  }

  class AdderTypeParmNotWork2(val size: Int)(using parent: GlobalInfo)
      extends UserModule {

    val a = newInput[size.type]("a") // val a = newInput[size.type]("a",size)
    val b = newInput[size.type]("b")
    val y = newOutput[size.type]("y")

    // m1.y := a - b // will both err
    y := a + b
  }

  /* module call */
  trait Adder1I[I <: Int] {
    val a: VarTyped[I]
    val b: VarTyped[I]
    val y: VarTyped[I]
  }

  class Adder1(using GlobalInfo) extends UserModule {

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    y := a - b
  }

  inline def adder1TypeParamMod[I <: Int: ValueOf](using GlobalInfo, ModLocalInfo) =
    new UserModule with Adder1I[I] {
      val a = newIO[I](VarType.Input)
      val b = newIO[I](VarType.Input)
      val y = newIO[I](VarType.Output)
      y := a - b
    }
  /* ok */
  class AdderComb4TypeParamMod(using GlobalInfo) extends UserModule {
// parent contains global info
    // inline val ii = 2
    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val c = newInput[2]("c")
    val d = newInput[2]("d")
    val y = newOutput[2]("y")

    val m1 = newMod(adder1TypeParamMod[2]) // might be able to rm this
    val m2 = newMod(adder1TypeParamMod[2]) // might be able to rm this
    // m1.y := a - b // will both err
    m1.a := a
    m1.b := b
    m2.a := c
    m2.b := d

    y := m1.y + m2.y
  }

}
