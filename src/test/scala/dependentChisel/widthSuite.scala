package dependentChisel

import dependentChisel.tests.adder.*
import dependentChisel.tests.untyped
import dependentChisel.tests.ifTest.*
import dependentChisel.testUtils.checkWidthAndFirrtl
import org.scalatest.funsuite.AnyFunSuite
import dependentChisel.tests.BubbleFifoErr
import dependentChisel.tests.adder

/* ATTN! set flags in global to turn on width check! */
class widthSuite extends AnyFunSuite {

  test("width check correct cases") {
    val corrCases = Seq(
      checkWidthAndFirrtl { implicit p =>
        new untyped.AdderUnTpCallUntp
      }
      /* checkWidthAndFirrtl { implicit p =>
        new untyped.AdderUnTpCallUntpWidthGt
      } */ // switch more strict width check
    )
    corrCases.foreach(x => assert(x._2, x._1))
  }

  test("width check incorrect cases") {

    val incorrectCases = Seq(
      checkWidthAndFirrtl { implicit p => new untyped.AdderUnTpCallUntpErr },
      checkWidthAndFirrtl { implicit p => new untyped.AdderUntpBug1 },
      checkWidthAndFirrtl { implicit p => new untyped.AdderUntpBug2 },
      checkWidthAndFirrtl { implicit p => new untyped.AdderUntpBug3typeCast },
      checkWidthAndFirrtl { implicit p =>
        new BubbleFifoErr.BubbleFifo(2, 3)
      },
      checkWidthAndFirrtl { implicit p =>
        new untyped.AdderUnTpCallUntpWidthGt
      } // switch more strict width check so this shall fail
    )

    incorrectCases.foreach(x => assert(!x._2, x._1))
  }
}
