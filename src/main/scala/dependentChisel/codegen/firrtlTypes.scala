package dependentChisel.codegen

import dependentChisel.algo.seqCmd2tree.AST
import com.doofin.stdScalaCross.*
import dependentChisel.typesAndSyntax.chiselModules.ModLocalInfo

object firrtlTypes {

  /** io part of one firrtl module */
  case class IOdef(
      name: String,
      tpe: String,
      width: Option[Int]
  )

  /** one firrtl module */
  case class FirrtlModule(modInfo: ModLocalInfo, io: List[IOdef], ast: AST)

  /** the whole circuit with multiple modules */
  case class FirrtlCircuit(mainModuleName: String, modules: List[FirrtlModule])

  /** binary firrtl Operators */
  enum firrtlOp {
    case sub, add
  }

  val firrtlOpMap =
    "+ add - sub * mul == eq := ="
      .split(" ")
      .grouped(2)
      .map(x => (x(0), x(1)))
      .toList
}
