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
import dependentChisel.codegen.sequentialCommands.Cmds
import dependentChisel.algo.Tree.TreeNode
import dependentChisel.codegen.sequentialCommands.Ctrl
import dependentChisel.codegen.sequentialCommands.Start
import dependentChisel.codegen.sequentialCommands.End
import dependentChisel.codegen.sequentialCommands.NewInstance
import dependentChisel.codegen.sequentialCommands.WeakStmt
import dependentChisel.codegen.sequentialCommands.VarDecls
import dependentChisel.codegen.sequentialCommands.Skip
import dependentChisel.codegen.sequentialCommands.BoolProp

/* more tests for parameterized mod*/
class astTransformSuite extends AnyFunSuite {
  test("tree AST transform works") {
    val m = makeModule({ implicit p =>
      new adder.Adder1prop
    })

    pprint.pprintln(m.moduleData.commandAsTree())
    val newAst =
      m.moduleData.transformTree { (ast: TreeNode[Ctrl | Cmds]) =>
        val predicate: Ctrl | Cmds => Boolean = {
          case BoolProp(name, prop) => true
          case _                    => false
        }

        treeTraverse.filterTop(predicate, ast)
      }

    pprint.pprintln(newAst)
  }

}
