package dependentChisel.tests

/* imperativeStyle dependent chisel */
import dependentChisel.syntax.ImperativeModules.*
import dependentChisel.*

// import datatypes.basicTypes.*
// import datatypes.statements.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

// import dependentChisel.imperativeDataTypes
import dependentChisel.syntax.tree.TopLevelCircuit
import dependentChisel.syntax.tree
import dependentChisel.typesAndSyntax.all.*
object adder extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val (mod, depInfo: globalInfo) = makeModule { implicit p =>
      new Adder1
    }
    pp(mod.modLocalInfo)
    val cmds = mod.modLocalInfo.commands
    println(codegen.firAST.genFirrtlStr(cmds.toList))
  }

  def run = {}
  class Adder1(using parent: globalInfo) extends UserModule {
// parent contains global info

    val a: Input[2] = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    y := a - b
  }

}
