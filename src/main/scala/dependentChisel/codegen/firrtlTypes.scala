package dependentChisel.codegen

import dependentChisel.algo.seqCmd2tree.AST

object firrtlTypes {

  /** io part of one firrtl module */
  case class IOdef(name: String, tpe: String, width: Option[Int])

  /** one firrtl module */
  case class FirrtlModule(name: String, io: List[IOdef], ast: AST)

  /** the whole circuit with multiple modules */
  case class fCircuits(mod: List[FirrtlModule], mainModule: String)
}
