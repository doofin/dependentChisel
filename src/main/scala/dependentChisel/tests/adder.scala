package dependentChisel.tests

/* imperativeStyle dependent chisel */
import dependentChisel.syntax.ImperativeModules.*
import dependentChisel.*

import chiselDataTypes.*
// import datatypes.basicTypes.*
// import datatypes.statements.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

// import dependentChisel.imperativeDataTypes
import dependentChisel.syntax.tree.TopLevelCircuit
import dependentChisel.syntax.tree

import dependentChisel.chiselDataTypes
object adder extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val (mod, depInfo: DependenciesInfo) = makeModule { implicit p =>
      new Adder1
    }
    // mod.create

    pp(mod.modLocalInfo)
  }

  class Adder1(using parent: DependenciesInfo) extends UserModule {
// parent contains global info

    val a: Input[2] = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    override def create = {
      y :== a - b
    }
  }

}
