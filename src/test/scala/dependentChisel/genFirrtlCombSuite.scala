package dependentChisel
import org.scalatest.funsuite.AnyFunSuite

import dependentChisel.tests.adder.*
import dependentChisel.tests.ifTest.*

import dependentChisel.tests.untyped
import dependentChisel.testUtils.checkWidthAndFirrtl
import dependentChisel.tests.BubbleFifo.*
import dependentChisel.tests.BubbleFifo

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import dependentChisel.tests.adder

/* more tests for parameterized mod*/
class genFirrtlCombSuite extends AnyFunSuite {
  val num = 6
  test("can pass firrtl compiler for FifoRegister(1) to FifoRegister(10)") {
    (1 to num map (i => checkWidthAndFirrtl { implicit p => new FifoRegister(i.refine) }))
      .foreach(x => assert(x._2, x._1))
  }

  test("can pass firrtl compiler for BubbleFifo(size, depth)") {

    val mods = for {
      size <- 2 to num
      depth <- 3 to num
    } yield (checkWidthAndFirrtl { implicit p => new BubbleFifo(size.refine, depth) })

    mods.foreach(x => assert(x._2, x._1))
  }

}
