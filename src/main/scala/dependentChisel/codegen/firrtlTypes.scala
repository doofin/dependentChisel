package dependentChisel.codegen

import dependentChisel.algo.seqCmd2tree.AST
import com.doofin.stdScalaCross.*
import dependentChisel.typesAndSyntax.chiselModules.ModLocalInfo
import dependentChisel.typesAndSyntax.typesAndOps.VarType

object firrtlTypes {

  /** io part of one firrtl module */
  case class IOdef(
      name: String,
      tpe: VarType.Input.type | VarType.Output.type,
      width: Option[Int]
  )

  /** one firrtl module */
  case class FirrtlModule(modInfo: ModLocalInfo, io: List[IOdef], ast: AST)

  /** the whole circuit with multiple modules */
  case class FirrtlCircuit(mainModuleName: String, modules: List[FirrtlModule])

  /** binary firrtl Operators: operator name to firrtl op name */
  val firrtlOpMap =
    "+ add - sub * mul == eq := = | or"
      .split(" ")
      .grouped(2)
      .map(x => (x(0), x(1)))
      .toList
}
