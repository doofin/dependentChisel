package dependentChisel

import dependentChisel.examples.adder.*
import dependentChisel.examples.dynamicAdder
import dependentChisel.examples.ifTest.*
import dependentChisel.testUtils.checkWidthAndFirrtl
import org.scalatest.funsuite.AnyFunSuite
import dependentChisel.examples.BubbleFifoErr
import dependentChisel.examples.adder
import dependentChisel.typesAndSyntax.chiselModules.GlobalInfo
import firrtl.FirrtlProtos.Firrtl.Module.UserModule

/* ATTN! set flags in global to turn on width check! */
class widthPassFailSuite extends AnyFunSuite {

  test("width check correct cases") {
    val corrCases = Seq(
      checkWidthAndFirrtl { implicit p =>
        new dynamicAdder.AdderDymCallStatic
      },
      checkWidthAndFirrtl { implicit p =>
        new adder.AdderMixed(2)
      }
    )
    corrCases.foreach(x => assert(x._2, x._1))
  }

  test("width check incorrect cases") {

    val incorrectCases = Seq(
      checkWidthAndFirrtl { implicit p => new dynamicAdder.AdderUnTpCallUntpErr },
      checkWidthAndFirrtl { implicit p => new dynamicAdder.AdderUntpBug1 },
      checkWidthAndFirrtl { implicit p => new dynamicAdder.AdderUntpBug2 },
      checkWidthAndFirrtl { implicit p => new dynamicAdder.AdderUntpBug3typeCast },
      checkWidthAndFirrtl { implicit p =>
        new BubbleFifoErr.BubbleFifoErr(2, 3)
      },
      checkWidthAndFirrtl { implicit p =>
        new dynamicAdder.AdderUnTpCallUntpWidthGt
      }, // switch more strict width check so this shall fail
      checkWidthAndFirrtl { implicit p =>
        new adder.AdderMixed(1)
      }
    )

    incorrectCases.foreach(x => assert(!x._2, x._1))
  }
}

/* checkWidthAndFirrtl { implicit p =>
        new untyped.AdderUnTpCallUntpWidthGt
      } */ // switch more strict width check

/* def t1[M <: UserModule]: Seq[GlobalInfo ?=> M] = Seq({ p ?=>
    new untyped.AdderUnTpCallUntp
  }) */
