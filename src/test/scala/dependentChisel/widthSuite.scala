package dependentChisel

import dependentChisel.tests.adder.*
import dependentChisel.tests.untyped
import dependentChisel.tests.ifTest.*
import dependentChisel.testUtils.widthAndFirrtlOk
import org.scalatest.funsuite.AnyFunSuite

class widthSuite extends AnyFunSuite {

  test("width check correct cases") {
    assert(testUtils.widthAndFirrtlOk { implicit p => new untyped.AdderUnTpCallUntp })

  }

  test("width check incorrect cases") {

    val incorrectCases = Seq(
      widthAndFirrtlOk { implicit p => new untyped.AdderUnTpCallUntpErr },
      widthAndFirrtlOk { implicit p => new untyped.AdderUntpBug1 },
      widthAndFirrtlOk { implicit p => new untyped.AdderUntpBug2 },
      widthAndFirrtlOk { implicit p => new untyped.AdderUntpBug3typeCast }
    )

    incorrectCases.foreach(x => assert(!x))
  }
}
