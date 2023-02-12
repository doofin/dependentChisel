/* imperativeStyle dependent chisel */
import dependentChisel.*
import syntax.imperative.*
import dataTypes.*

import com.doofin.stdScalaCross.*

class mod1(using m: DependenciesInfo) extends UserModule {
//   val a = Input[2]()
//   val b = Input[2]()
//   val y = Output[2]()

  val a = newInput[2]
  val b = newInput[2]
  val y = newOutput[2]

//   val a = Input(2)
//   val b = Input(2)
//   val y = Output(2)
  y := a + b
}

class mod2(using m: DependenciesInfo) extends UserModule {
  val a = newInput[2]
  val b = newInput[2]
  val y = newOutput[2]

  val m1 = new mod1

  y := a + b
//   a := a + b
}

val d = instModule { implicit p => new mod2 }

d.names.toList
val outInfo = d.mods.toList.map(x => (x.name, x.modCircuits.io.toList, x.modCircuits.commands.toList))
pp(outInfo)

BitsType.UInt[1]().valu

InputB(BitsType.Bits[1]()) + InputB(BitsType.UInt[1]())
