package dependentChisel
import org.scalatest.funsuite.AnyFunSuite

import dependentChisel.tests.adder.*
import dependentChisel.tests.ifTest.*

import dependentChisel.tests.untyped
import dependentChisel.testUtils.checkWidthAndFirrtl
import dependentChisel.tests.BubbleFifo.*
import dependentChisel.tests.BubbleFifo

import io.github.iltotore.iron.autoRefine

class genFirrtlSuite extends AnyFunSuite { // AnyFunSuite  munit.FunSuite

  test("can pass firrtl compiler down to verilog") {
    Seq(
      checkWidthAndFirrtl { implicit p => new Adder1 },
      // widthAndFirrtlOk { implicit p => new AdderTypeParm1[2] }, // not work
      checkWidthAndFirrtl { implicit p => new AdderCall1 },
      checkWidthAndFirrtl { implicit p => new AdderComb4 },
      checkWidthAndFirrtl { implicit p => new IfModNested },
      checkWidthAndFirrtl { implicit p => new FifoRegister(5) },
      checkWidthAndFirrtl { implicit p => new BubbleFifo(1, 2) },
      checkWidthAndFirrtl { implicit p => new BubbleFifo(3, 5) },
      checkWidthAndFirrtl { implicit p => new AdderTypeParm3 }
    ).foreach(x => assert(x._2, x._1))
  }

}
