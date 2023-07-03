package dependentChisel.tests

import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.typesAndSyntax.varDecls.*
import dependentChisel.codegen.compiler.*
import dependentChisel.tests.adder

import adder.*
object untyped extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val mod = makeModule { implicit p =>
      // new AdderUnTpCallUntp
      new AdderUntpBug3typeCast
      // new DoubleAdder3(2)
      // new AdderUntpBug
      // new AdderUntp1
    }

    chiselMod2verilog(mod)
  }

// ok
  class AdderDym(using parent: GlobalInfo) extends UserModule {

    val a = newIODym(2, VarType.Input)
    val b = newIODym(2, VarType.Input)
    val y = newIODym(2, VarType.Output)

    y := a + b
  }

  class AdderUntp1Param(using parent: GlobalInfo)(width: Int) extends UserModule {

    val a = newIODym(width, VarType.Input)
    val b = newIODym(width, VarType.Input)
    val y = newIODym(width, VarType.Output)

    y := a + b
  }

// ok, typed call untyped

  class AdderCallDym(using GlobalInfo) extends UserModule {

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    val m1 = newMod(new AdderDym)
    m1.a := a
    m1.b := b
    y := m1.y.asTyped[2]
  }

//  ok, untyped  call typed
  class AdderUnTpCallUntp(using parent: GlobalInfo) extends UserModule {

    val a = newInputDym(2)
    val b = newInputDym(2)
    val y = newOutputDym(2)

    val m1 = newMod(new Adder1)
    m1.a := a.asTyped[2]
    m1.b := b.asTyped[2]
    y := m1.y
  }

  class AdderUnTpCallUntpErr(using parent: GlobalInfo) extends UserModule {

    val a = newInputDym(2)
    val b = newInputDym(2)
    val y = newOutputDym(1)

    val m1 = newMod(new AdderDym)
    m1.a := a
    m1.b := b
    y := m1.y // not allowed since for width, lhs < rhs
  }

  /** allow width mismatch if lhs>rhs */
  class AdderUnTpCallUntpWidthGt(using parent: GlobalInfo) extends UserModule {

    val a = newInputDym(1)
    val b = newInputDym(2)
    val y = newOutputDym(2)

    val m1 = newMod(new AdderDym)
    m1.a := a // allowed since for width, lhs > rhs
    m1.b := b
    y := m1.y
  }

  class AdderUnTpCallTpWidthErr(using parent: GlobalInfo) extends UserModule {

    val a = newInputDym(2)
    val b = newInputDym(2)
    val y = newOutputDym(1)

    val m1 = newMod(new Adder1)
    m1.a := a.asTyped[2] // can't detect this due to cast!!
    m1.b := b.asTyped[2]
    y := m1.y
  }

  // firrtl allow this?
  class AdderUntpBug1(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInputDym(20)
    val b = newInputDym(20)
    val y = newOutputDym(10)

    y := a - b
  }

  class AdderUntpBug2(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInputDym(20)
    val b = newInputDym(10)
    val y = newOutputDym(20)

    y := a - b + a - b
  }

  class AdderUntpBug3typeCast(using parent: GlobalInfo) extends UserModule {

    val a = newInputDym(1)
    val b = newInputDym(2)
    val y = newOutputDym(2)

    val m1 = newMod(new Adder1)
    m1.a := a.asTyped[2]
    m1.b := b.asTyped[2]
    y := m1.y
  }
}
