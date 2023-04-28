/*
package dependentChisel
import org.scalatest.funsuite.AnyFunSuite

import dependentChisel.tests.adder.*
import dependentChisel.tests.ifTest.*

import dependentChisel.tests.untyped
import dependentChisel.testUtils.widthAndFirrtlOk
import dependentChisel.tests.BubbleFifo.*
import dependentChisel.tests.BubbleFifo

 class genFirrtlCombSuite extends AnyFunSuite {

  test("can pass firrtl compiler for FifoRegister(1) to FifoRegister(10)") {
    (1 to 10 map (i => widthAndFirrtlOk { implicit p => new FifoRegister(i) })).foreach(
      x => assert(x._2, x._1)
    )
  }

  test("can pass firrtl compiler for BubbleFifo(size, depth)") {

    val mods = for {
      size <- 1 to 6
      depth <- 3 to 8
    } yield (widthAndFirrtlOk { implicit p => new BubbleFifo(size, depth) })

    mods.foreach(x => assert(x._2, x._1))
  }

} */
