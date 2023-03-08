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
object ifTest extends mainRunnable {
  // var globalDepInfo

  override def main(args: Array[String] = Array()): Unit = {
    val (mod, depInfo: globalInfo) = makeModule { implicit p =>
      new IfModNested
    }

    pp(mod.modLocalInfo)
    val cmds = mod.modLocalInfo.commands
    println(codegen.firAST.gen(cmds.toList))

  }

  class IfElse1(using parent: globalInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val y = newOutput[16]("y")

    IfElse(a === b) {
      y := a + b
    } {
      y := a - b
    }
  }

  class IfMod(using parent: globalInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val y = newOutput[16]("y")
    val y2 = newOutput[16]("y2")

    y := a - b
    If(a === b) {
      y := a + b
      y2 := a - b
    }
  }

// UserModuleTop contains global info
  class IfModNested(using parent: globalInfo) extends UserModule {
    val a = newInput[16]("a")
    val b = newInput[16]("b")
    val c = newInput[16]("b")
    val y = newOutput[16]("y")

    y := a - b
    If(a === b) {
      y := a + b
      If(a === c) {
        y := a * b
      }
    }
  }
}
