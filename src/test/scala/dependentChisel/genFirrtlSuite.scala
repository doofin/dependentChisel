package dependentChisel

import dependentChisel.tests.adder.*
import dependentChisel.tests.untyped
import dependentChisel.tests.ifTest.*
import chisel3.assert

class genFirrtlSuite extends munit.FunSuite {

  test("can gen firrtl") {
    assert(testUtils.canPassFirrtl { implicit p => new DoubleAdder })

    assert(testUtils.canPassFirrtl { implicit p => new IfModNested })
  }

  test("width check") {

    assert(testUtils.canPassFirrtl { implicit p => new untyped.AdderUnTpCallUntpErr })
  }
}
