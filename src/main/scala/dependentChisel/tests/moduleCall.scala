package dependentChisel.tests

/* imperativeStyle dependent chisel */
import dependentChisel.syntax.ImperativeModules.*
import dependentChisel.*
import chiselDataTypes.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.chiselDataTypes

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

  class UserMod1(using parent: DependenciesInfo) extends UserModule {
// parent contains global info
    val a = newInput[2]("a") // ModCircuits is a implicit in this class
    val b = newInput[2]("b")
    val y = newOutput[2]("y")
    // create
    override def create = { y :== a + b }
    create
  }

  class UserMod2(using parent: DependenciesInfo) extends UserModule {
    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")
    val m1 = new UserMod1 // gen some uuid?
    val m2 = new UserMod1

    override def create = {
      val port = if (1 to 2).sum == 4 then m1.y else m2.y
      port :== a + b // will only show as port,
    }
    create
  }

}
