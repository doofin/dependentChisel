package dependentChisel.syntax

import dependentChisel.typesAndSyntax.typesAndOps.VarType
import dependentChisel.typesAndSyntax.typesAndOps.Lit
import dependentChisel.typesAndSyntax.typesAndOps.VarType.RegInit

object naming {
  var n: Int = 0

  def genNameForVar(givenName: String, tp: VarType) = {

    genNameIfEmpty(
      givenName,
      tp match {
        case VarType.Input  => "io_i"
        case VarType.Output => "io_o"
        case VarType.Reg    => "r"
        case VarType.Wire   => "wi"
        case RegInit(init)  => "ri"
      }
    )
  }

  def genNameIfEmpty(givenName: String, prefix: String) = {
    s"${if givenName.isEmpty() then prefix else givenName}${naming.getIdWithDash}"
  }

  def getIdWithDash = {
    "_" + getIntId
  }

  def getIntId = {
    n += 1
    n
  }

  def mkUidFrom(s: String) = s + getIdWithDash

  // class Counter() {}
}
