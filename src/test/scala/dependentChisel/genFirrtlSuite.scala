package dependentChisel
import org.scalatest.funsuite.AnyFunSuite

import dependentChisel.tests.adder.*
import dependentChisel.tests.ifTest.*

import dependentChisel.tests.untyped
import dependentChisel.testUtils.widthAndFirrtlOk
import dependentChisel.tests.BubbleFifo.*
import dependentChisel.tests.BubbleFifo

class genFirrtlSuite extends AnyFunSuite {

  test("can pass firrtl compiler down to verilog") {
    Seq(
      widthAndFirrtlOk { implicit p => new Adder1 },
      // widthAndFirrtlOk { implicit p => new AdderTypeParm1[2] }, // not work
      widthAndFirrtlOk { implicit p => new AdderCall1 },
      widthAndFirrtlOk { implicit p => new AdderComb4 },
      widthAndFirrtlOk { implicit p => new IfModNested },
      widthAndFirrtlOk { implicit p => new FifoRegister(5) },
      widthAndFirrtlOk { implicit p => new BubbleFifo(1, 2) },
      widthAndFirrtlOk { implicit p => new BubbleFifo(3, 5) }
    ).foreach(x => assert(x._2, x._1))
  }

}
