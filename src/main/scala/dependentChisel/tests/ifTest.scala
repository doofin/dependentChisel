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
object ifTest extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val (mod, depInfo: DependenciesInfo) = makeModule { implicit p =>
      new IfElse1
    }
    // mod.create

    pp(mod.modLocalInfo)
  }

  class IfElse1(using parent: DependenciesInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val y = newOutput[16]("y")
    val y2 = newOutput[16]("y2")

    override def create = {
      y :== a - b
      IfElse(a === b) {
        y :== a + b
        y2 :== a - b
      } {
        y :== a - b
        y2 :== a + b
      }
    }
    create
  }

  class IfMod2(using parent: DependenciesInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val y = newOutput[16]("y")
    val y2 = newOutput[16]("y2")

    override def create = {
      y :== a - b
      If(a === b) {
        y :== a + b
        y2 :== a - b
      }
    }
    create
  }

  class UserMod1(using parent: DependenciesInfo) extends UserModule {
// parent contains global info

    val a: Input[2] = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    override def create = {
      y :== a - b
      /*       If(a === b) {
        y :== a + b
      }
       */
    }
  }

}
