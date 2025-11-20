package dependentChisel
import org.scalatest.funsuite.AnyFunSuite

import dependentChisel.examples.adder.*
import dependentChisel.examples.ifTest.*

import dependentChisel.examples.dynamicAdder
import dependentChisel.testUtils.checkWidthAndFirrtl
import dependentChisel.examples.BubbleFifo.*
import dependentChisel.examples.BubbleFifo

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import dependentChisel.examples.adder
import dependentChisel.typesAndSyntax.chiselModules.makeModule
import dependentChisel.algo.treeTraverse
import dependentChisel.algo.treeTraverse.filter

/* more tests for parameterized mod*/
class astTransform extends AnyFunSuite {
  test("tree AST transform works") {
    val m = makeModule({ implicit p =>
      new adder.Adder1prop
    })

    m.moduleData.transformTree { ast =>
      treeTraverse.filterTop({ t => true }, ast)
    }
  }

}
