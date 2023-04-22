package dependentChisel.tests

import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*

import dependentChisel.codegen.compiler.*
import dependentChisel.typesAndSyntax.chiselModules.*

import adder.*
object untyped extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val (mod, depInfo: GlobalInfo) = makeModule { implicit p =>
      // new AdderUnTpCallUntp // wid check not ok for inter module
      // new DoubleAdder3(2)
      new AdderUntpBug
      // new AdderUntp1
    }
    val fMod = chiselMod2firrtlCircuits(mod)
    // pp(fMod.modules map (_.modInfo))
    val firCirc = firrtlCircuits2str(fMod)
    println(firCirc)

    val verilog = firrtlUtils.firrtl2verilog(firCirc)
    // println(verilog)
  }

// ok
  class AdderUntp1(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInputDym(2)
    val b = newInputDym(2)
    val y = newOutputDym(2)

    y := a - b
  }

// ok, typed call untyped
  class AdderTpCallUntp(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    val m1 = newMod(new AdderUntp1)
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

  // firrtl allow this?
  class AdderUntpBug(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInputDym(2)
    val b = newInputDym(20)
    val y = newOutputDym(10)

    y := a - b
  }
}
