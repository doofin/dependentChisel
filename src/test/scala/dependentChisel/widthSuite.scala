package dependentChisel

import dependentChisel.tests.adder.*
import dependentChisel.tests.untyped
import dependentChisel.tests.ifTest.*
import dependentChisel.testUtils.widthAndFirrtlOk
import org.scalatest.funsuite.AnyFunSuite
import dependentChisel.tests.BubbleFifoErr

/* ATTN! set flags in global to turn on width check! */
class widthSuite extends AnyFunSuite {

  test("width check correct cases") {
    val corrCases = Seq(
      widthAndFirrtlOk { implicit p =>
        new untyped.AdderUnTpCallUntp
      },
      widthAndFirrtlOk { implicit p =>
        new untyped.AdderUnTpCallUntpWidthGt
      }
    )
    corrCases.foreach(x => assert(x._2, x._1))
  }

  test("width check incorrect cases") {

    val incorrectCases = Seq(
      widthAndFirrtlOk { implicit p => new untyped.AdderUnTpCallUntpErr },
      widthAndFirrtlOk { implicit p => new untyped.AdderUntpBug1 },
      widthAndFirrtlOk { implicit p => new untyped.AdderUntpBug2 },
      widthAndFirrtlOk { implicit p => new untyped.AdderUntpBug3typeCast },
      widthAndFirrtlOk { implicit p => new BubbleFifoErr.BubbleFifo(2, 3) }
    )

    incorrectCases.foreach(x => assert(!x._2, x._1))
  }
}
