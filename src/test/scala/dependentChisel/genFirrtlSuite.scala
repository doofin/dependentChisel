package dependentChisel

import dependentChisel.tests.adder.*
import dependentChisel.tests.untyped
import dependentChisel.tests.ifTest.*
import dependentChisel.testUtils.widthAndFirrtlOk
import org.scalatest.funsuite.AnyFunSuite

class genFirrtlSuite extends AnyFunSuite {

  test("can pass firrtl compiler") {
    Seq(
      widthAndFirrtlOk { implicit p => new Adder1 },
      // widthAndFirrtlOk { implicit p => new AdderTypeParm1[2] }, // not work
      widthAndFirrtlOk { implicit p => new DoubleAdder },
      widthAndFirrtlOk { implicit p => new IfModNested }
    ).foreach(assert(_))
  }

}
