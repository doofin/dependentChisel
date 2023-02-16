/* imperativeStyle dependent chisel */
import dependentChisel.*
import syntax.imperative.*
import dataTypes.*

import com.doofin.stdScalaCross.*
import dependentChisel.macros.inspectSimple1
import dependentChisel.macros.inspect

class mod1(using m: DependenciesInfo) extends UserModule {

  val a = newInput[2]("a")
  val b = newInput[2]("b")
  val y = newOutput[2]("y")

  y := a + b
}

class mod2(using m: DependenciesInfo) extends UserModule {
  val a = newInput[2]("a")
  val b = newInput[2]("b")
  val y = newOutput[2]("y")

  val m1 = new mod1

  y := a + b
  m1.y := a + b // becomes y := a + b,must gen names for it
  println("mod2")
  println(inspect(m1.y))
//   a := a + b
}

val d = makeModule { implicit p => new mod2 }

d.names.toList
val outInfo = d.modules.toList.map(x => (x.name, x.modCircuits))
pp(outInfo)
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
