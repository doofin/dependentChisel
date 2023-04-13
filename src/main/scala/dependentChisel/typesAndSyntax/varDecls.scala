package dependentChisel.typesAndSyntax

import com.doofin.stdScalaCross.*

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*

import dependentChisel.codegen.seqCommands.*

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.syntax.naming
import dependentChisel.codegen.firrtlTypes.IOdef

import scala.compiletime.*
import dependentChisel.typesAndSyntax.control.UserModuleOps

/* decls for variables like Wire, Reg, and IO
 type info is converted to value by constValueOpt
 */
object varDecls {
  trait UserModuleDecls { ut: UserModule & UserModuleOps =>
    inline def newInput[w <: Int](
        givenName: String = ""
    ) = {
      val m = modLocalInfo
      val genName =
        s"${if givenName.isEmpty() then "io_i" else givenName}${naming.getIdWithDash}"
      val instNm = ut.thisInstanceName
      val r = Input[w](instNm, genName) // instName + "." + name
      m.io.prepend(IOdef(r.instName, r.name, "input", constValueOpt[w]))
      dbg(r)
      // m.commands
      r
    }

    inline def newOutput[w <: Int](
        givenName: String = ""
    ) = {
      val m = modLocalInfo

      val genName =
        s"${if givenName.isEmpty() then "io_o" else givenName}${naming.getIdWithDash}"
      val instNm = ut.thisInstanceName
      val r = Output[w](instNm, genName)
      m.io.prepend(IOdef(r.instName, r.name, "output", constValueOpt[w]))
      r
    }

    inline def newReg[w <: Int](
        givenName: String = ""
    ) = {
      val m = modLocalInfo
      // m.commands
      val genName =
        s"${if givenName.isEmpty() then "io_o" else givenName}${naming.getIdWithDash}"
      val instNm = ut.thisInstanceName
      val r = Output[w](instNm, genName)
      m.io.prepend(IOdef(r.instName, r.name, "output", constValueOpt[w]))
      r
    }
  }

  /* def switch[condW <: Int](using
      m: ModLocalInfo
  )(cond: Var[condW])(cases: (Expr[condW], Stmt)*) = {
    // nested stmt? exhaustive check?
    // m.io.prepend(s"input ${name}[${constValueOpt[w]}]")
    // case classs switch
    Stmt.StmtNoOp
  } */

  /* Combinational Circuits */
}
