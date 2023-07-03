package dependentChisel.tests

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.*
import dependentChisel.typesAndSyntax.chiselModules.*

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*
import dependentChisel.codegen.compiler.*
object moduleCall extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {

    val mod = makeModule { implicit p => new UserMod2 }
    val fMod = chiselMod2firrtlCircuits(mod)
    val firCirc = firrtlCircuits2str(fMod)
    println(firCirc)
    firrtlUtils.firrtl2verilog(firCirc)
  }

  class UserMod1(using parent: GlobalInfo) extends UserModule {
// parent contains global info
    val a = newInput[2]("a") // ModCircuits is a implicit in this class
    val b = newInput[2]("b")
    val y = newOutput[2]("y")
    // create
    y := a + b
  }

  class UserMod2(using parent: GlobalInfo) extends UserModule {
    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    val m1 = newMod(new UserMod1) // gen some uuid?
    val m2 = newMod(new UserMod1)

    val port = if (1 to 2).sum == 4 then m1.y else m2.y
    port := a + b // will only show as port,
    m1.y := a + b
  }

}
