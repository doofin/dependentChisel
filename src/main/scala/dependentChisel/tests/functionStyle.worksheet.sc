import dependentChisel.syntax.ImperativeModules.*
import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.syntax.tree.TopLevelCircuit
import dependentChisel.syntax.tree
import dependentChisel.typesAndSyntax.all.*

import dependentChisel.tests.adder.*

import dependentChisel.codegen.firAST.*
/*  */
val (mod, depInfo: globalInfo) = makeModule { implicit p =>
  new Adder1
}
// mod.create

pp(mod.modLocalInfo)
val cmds = mod.modLocalInfo.commands
println(codegen.firAST.genFirrtlStr(cmds.toList))

ioTransformRhs(Input[1]("a") + Input[1]("a"))
