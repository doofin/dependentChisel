package dependentChisel.tests

/* imperativeStyle dependent chisel */
import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

object moduleCall extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {

    val (mod, globalDepInfo) = makeModule { implicit p => new UserMod2 }
    // mod.create
    // pp(mod.modLocalInfo)
    // pp(dep.names.toList)
    // mod.parent
    val outInfo =
      globalDepInfo.modules.toList.map(_.modLocalInfo)
    pp(outInfo)
    // pp(dep)
  }

  class UserMod1(using parent: globalInfo) extends UserModule {
// parent contains global info
    val a = newInput[2]("a") // ModCircuits is a implicit in this class
    val b = newInput[2]("b")
    val y = newOutput[2]("y")
    // create
    y := a + b
  }

  class UserMod2(using parent: globalInfo) extends UserModule {
    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    val m1 = new UserMod1 // gen some uuid?
    val m2 = new UserMod1

    val port = if (1 to 2).sum == 4 then m1.y else m2.y
    port := a + b // will only show as port,
    m1.y := a + b
  }

  class UserMod3(using parent: globalInfo)(val a: Input[2]) extends UserModule {
    // val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")

    val m1 = new UserMod1 // gen some uuid?
    val m2 = new UserMod1

    val port = if (1 to 2).sum == 4 then m1.y else m2.y
    port := a + b // will only show as port,
    m1.y := a + b
  }
}
