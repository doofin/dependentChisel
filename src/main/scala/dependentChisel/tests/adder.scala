package dependentChisel.tests

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

object adder extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val (mod, depInfo: globalInfo) = makeModule { implicit p =>
      new Adder1
    }
    pp(mod.modLocalInfo)
    val cmds = mod.modLocalInfo.commands
    // println(codegen.firAST.genFirrtlStr(cmds.toList))
  }

  class Adder1(using parent: globalInfo) extends UserModule {
// parent contains global info

    val a: Input[2] = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    y := a - b
  }

}
