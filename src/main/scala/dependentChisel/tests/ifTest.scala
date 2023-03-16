package dependentChisel.tests

/* imperativeStyle dependent chisel */
import dependentChisel.syntax.ImperativeModules.*
import dependentChisel.*

// import datatypes.basicTypes.*
// import datatypes.statements.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*
object ifTest extends mainRunnable {
  // var globalDepInfo

  override def main(args: Array[String] = Array()): Unit = run

  def run = {
    val (mod, depInfo: globalInfo) = makeModule { implicit p =>
      new IfModNested
    }

    // pp(mod.modLocalInfo)
    val cmds = mod.modLocalInfo.commands
    pp(cmds)
    // println(codegen.firAST.genFirrtlStr(cmds.toList))

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
    /* should require initialized y2:=0
within firrtl or before firrtl
maybe static analysis
     */
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
