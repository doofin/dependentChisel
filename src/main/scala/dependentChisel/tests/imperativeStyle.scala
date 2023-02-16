package dependentChisel.tests

/* imperativeStyle dependent chisel */
// import dependentChisel.syntax
import dependentChisel.syntax.ImperativeModules.*
import dependentChisel.*
import dataTypes.*

import com.doofin.stdScalaCross.*
import dependentChisel.macros.inspectSimple1
import dependentChisel.macros.inspect
import com.doofin.stdScala.mainRunnable
import dependentChisel.macros.getVarName

object imperativeStyle extends mainRunnable {

  override def main(args: Array[String]): Unit = {
    val d = makeModule { implicit p => new UserMod2 }

    pp(d.names.toList)
    val outInfo = d.modules.toList.map(x => (x.name, x.modCircuits))
    pp(outInfo)
  }

  class UserMod1(using m: DependenciesInfo) extends UserModule {

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")
    y := a + b
  }

  class UserMod2(using m: DependenciesInfo) extends UserModule {
    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val y = newOutput[2]("y")
    val m1 = new UserMod1

    m1.y := a + b
    // Output("io.y"):=
    // if (1 to 10).sum == 10 then m1.y else m1.y := a + b
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
