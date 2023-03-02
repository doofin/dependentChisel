package dependentChisel.tests

/* imperativeStyle dependent chisel */
import dependentChisel.syntax.ImperativeModules.*
import dependentChisel.*
import chiselDataTypes.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.chiselDataTypes

object moduleCallOld extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    val d = makeModule { implicit p => new UserMod2 }._2

    // pp(d.names.toList) // list of mods
    val outInfo = d.modules.toList.map(x => (x.thisClassName, x.modLocalInfo))
    pp(outInfo)
  }

  class UserMod1(using parent: DependenciesInfo) extends Module {
// parent contains global info
    val a = newInput[2]("a") // ModCircuits is a implicit in this class
    val b = newInput[2]("b")
    val y = newOutput[2]("y")
    y :== a + b
  }

  class UserMod2(using parent: DependenciesInfo) extends Module {
    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")
    val m1 = new UserMod1 // gen some uuid?
    val m2 = new UserMod1

    // m1.y := a + b
    // Output("io.y"):=
    val port = if (1 to 2).sum == 4 then m1.y else m2.y
    /* UserMod1.y_6 := Input(UserMod2.a_1) + Input(UserMod2.b_2),width=Some(2)"
    "UserMod1.y_9 := Input(UserMod2.a_1) + Input(UserMod2.b_2),width=Some(2)"
     */
    port :== a + b // will only show as port,
  }

  // m1.y := a + b // becomes y := a + b,must gen names for it
  // println("mod2")
  // println("inspect" + inspect(m1.y))
  // println(" getVarName" + getVarName(m1.y))
//   a := a + b

// can print io ports,circuits and int width,lack varname ,bits type(uint,sint), more operators

// BitsType.UInt[1]().valu

// InputB(BitsType.Bits[1]()) + InputB(BitsType.UInt[1]())

  /*
val a = newInput[2]
  val b = newInput[2]
  val y = newOutput[2]

  //   val a = Input[2]()
//   val b = Input[2]()
//   val y = Output[2]()

   */

}
