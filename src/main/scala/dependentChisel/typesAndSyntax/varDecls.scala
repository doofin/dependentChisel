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
      val genName = naming.genNameIfEmpty(givenName, "io_i")
      val instName = ut.thisInstanceName

      val r = Input[w](instName + "." + genName) // instName + "." + name
      modLocalInfo.io.prepend(
        IOdef(genName, "input", constValueOpt[w])
      )
      r
    }

    inline def newOutput[w <: Int](
        givenName: String = ""
    ) = {
      val genName =
        naming.genNameIfEmpty(givenName, "io_o")

      val instName = ut.thisInstanceName

      val r = Output[w](
        instName + "." + genName
      ) // instName + "." + name
      modLocalInfo.io.prepend(
        IOdef(genName, "output", constValueOpt[w])
      )
      r
    }

    def newInputDym(width: Int, givenName: String = "") = {
      val m = modLocalInfo
      val genName =
        s"${if givenName.isEmpty() then "io_i" else givenName}${naming.getIdWithDash}"

      val instName = ut.thisInstanceName
      val r = VarDymTyped(
        width,
        VarDeclTp.Input,
        instName + "." + genName
      ) // when refered in expr , use this name
      m.io.prepend(
        IOdef(r.name, "input", Some(width))
      ) // when put in io bundle,use short name
      r
    }

    def newRegDym(width: Int, givenName: String = "") = {
      val m = modLocalInfo
      // m.commands
      val genName =
        s"${if givenName.isEmpty() then "reg" else givenName}${naming.getIdWithDash}"
      val r = VarDymTyped(width, VarDeclTp.Reg, genName)
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
