package dependentChisel

import dependentChisel.tests.adder.*
import dependentChisel.tests.untyped
import dependentChisel.tests.ifTest.*
import dependentChisel.testUtils.widthAndFirrtlOk
import org.scalatest.funsuite.AnyFunSuite

class genFirrtlSuite extends AnyFunSuite {

  test("can pass firrtl compiler") {
    assert(testUtils.widthAndFirrtlOk { implicit p => new DoubleAdder })
    assert(testUtils.widthAndFirrtlOk { implicit p => new IfModNested })
  }

}
