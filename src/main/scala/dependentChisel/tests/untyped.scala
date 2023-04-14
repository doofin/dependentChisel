package dependentChisel.tests

import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*

import dependentChisel.codegen.compiler.*
import dependentChisel.typesAndSyntax.chiselModules.*

object untyped extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val (mod, depInfo: GlobalInfo) = makeModule { implicit p =>
      new AdderUntp1
    // new DoubleAdder3(2)
    }
    val fMod = chiselMod2firrtlCircuits(mod)
    // pp(fMod.modules map (_.modInfo))
    val firCirc = firrtlCircuits2str(fMod)
    println(firCirc)

    val verilog = firrtlUtils.firrtl2verilog(firCirc)
    println(verilog)
  }

  class AdderUntp1(using parent: GlobalInfo) extends UserModule {
// parent contains global info

    val a = newInputDym(2)
    val b = newInputDym(2)
    val y = newOutputDym(2)

    y := a - b
  }
}
