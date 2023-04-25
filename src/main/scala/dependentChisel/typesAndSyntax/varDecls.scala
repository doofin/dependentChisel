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

// TODO
  def newReg[w <: Int, tp <: VarType](width: Int, givenName: String = "") = {
    val genName =
      s"${if givenName.isEmpty() then "reg" else givenName}${naming.getIdWithDash}"
    // VarTyped[w, tp](genName)
  }

  /** allow to be called outside module */
  inline def newIO[w <: Int](using
      mli: ModLocalInfo
  )(tp: VarType.Input.type | VarType.Output.type, givenName: String = "") = {

    val genName = naming.genNameForVar(givenName, tp)
    val r = VarTyped[w](
      mli.thisInstanceName + "." + genName,
      tp
    )
    val width = constValueOpt[w]

    mli.typeMap.addOne(r, width)
    mli.io.prepend(IOdef(genName, tp, width))
    r
  }

  def newIODym[w <: Int](using
      mli: ModLocalInfo
  )(width: Int, tp: VarType.Input.type | VarType.Output.type, givenName: String = "") = {

    val genName = naming.genNameForVar(givenName, tp)
    val r = VarDymTyped(
      width,
      tp,
      mli.thisInstanceName + "." + genName
    ) // when refered in expr , use this name

    mli.typeMap.addOne(r, Some(width))
    mli.io.prepend(IOdef(genName, tp, Some(width)))
    // when put in io bundle,use short name
    r
  }

  /** stmts only allowed inside a module */
  trait UserModuleDecls { ut: UserModule & UserModuleOps =>
    def newRegDym(width: Int, givenName: String = "") = {
      // need to push this cmd for varDecl
      val genName =
        s"${if givenName.isEmpty() then "reg" else givenName}${naming.getIdWithDash}"
      val r = VarDymTyped(width, VarType.Reg, genName)
      modLocalInfo.commands.append(VarDecls(r))
      r
    }
    inline def newInput[w <: Int](
        givenName: String = ""
    ) = newIO[w](VarType.Input, givenName)

    inline def newOutput[w <: Int](
        givenName: String = ""
    ) = newIO[w](VarType.Output, givenName)

    def newInputDym(width: Int, givenName: String = "") = {
      // appendIO_untyped(width, VarType.Input, givenName)
      newIODym(width, VarType.Input, givenName)
    }

    def newOutputDym(width: Int, givenName: String = "") = {
      newIODym(width, VarType.Output, givenName)
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
