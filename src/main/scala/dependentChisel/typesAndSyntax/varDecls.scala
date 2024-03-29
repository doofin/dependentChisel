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
import dependentChisel.codegen.compiler

/* decls for variables like Wire, Reg, and IO
 type info is converted to value by constValueOpt
 */
object varDecls {

  /** use width in type param first,then try with width: Option[Int] in param. if both are
    * not provided then auto infer the width
    */
  inline def newLit[w <: Int](v: Int, width: Option[Int] = None) = {
    /* example : 199 is UInt<8>("hc7")
    type of lit: {UnsignedInt, SignedInt, HexLit, OctalLit, BinaryLit}
    output (hexStr,ceiling width) */
    val calcWidth = compiler.int2hexAndCeiling(v)._2
    val w = constValueOpt[w].orElse(width).getOrElse(calcWidth)
    // dbg(v, calcWidth, w, width)
    LitDym(v, w)
  }

  inline def newLitp[w <: Int: ValueOf](v: Int, width: Option[Int] = None) = {

    val width = constValueOpt[w].getOrElse(valueOf[w])
    LitDym(v, width).asTypedUnsafe[w]
  }

  /** allow to be called outside module */
  inline def newIO[w <: Int: ValueOf](using
      mli: ModLocalInfo
  )(
      tp: VarType.Input.type | VarType.Output.type,
      // widthOpt: Option[Int] = None,
      givenName: String = ""
  ) = {

    val genName = naming.genNameForVar(givenName, tp)
    val r = VarTyped[w](
      mli.thisInstanceName + "." + genName,
      tp
    )
    val width = constValueOpt[w].getOrElse(valueOf[w]) // .orElse(widthOpt)
    // dbg(r, width)
    mli.typeMap.addOne(r, width)
    mli.io.prepend(IOdef(genName, tp, Some(width)))
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

    mli.typeMap.addOne(r, width)
    mli.io.prepend(IOdef(genName, tp, Some(width)))
    // when put in io bundle,use short name
    r
  }

  /** stmts only allowed inside a module */
  trait UserModuleDecls { ut: UserModule & UserModuleOps =>
    def newReg[w <: Int: ValueOf](givenName: String = "") = {
      // need to push this cmd for varDecl
      val genName = naming.genNameForVar(givenName, VarType.Reg)

      val r = VarTyped[w](
        genName,
        VarType.Reg
      )

      val width = constValueOpt[w].getOrElse(valueOf[w]) // .orElse(widthOpt)
      modLocalInfo.typeMap.addOne(r, width)
      modLocalInfo.commands.append(VarDecls(r.toDym(width)))
      r
    }

    def newRegDym(width: Int, givenName: String = "") = {
      // need to push this cmd for varDecl
      val genName = naming.genNameForVar(givenName, VarType.Reg)
      val r = VarDymTyped(width, VarType.Reg, genName)
      modLocalInfo.typeMap.addOne(r, width)
      modLocalInfo.commands.append(VarDecls(r))
      r
    }

    def newRegInitDym(init: LitDym, givenName: String = "") = {
      // need to push this cmd for varDecl
      val width = init.width
      val genName = naming.genNameForVar(givenName, VarType.Reg)
      val r = VarDymTyped(width, VarType.RegInit(init), genName)
      modLocalInfo.typeMap.addOne(r, width)
      modLocalInfo.commands.append(VarDecls(r))
      r
    }

    inline def newInput[w <: Int: ValueOf](
        givenName: String = ""
    ) = newIO[w](VarType.Input, givenName)

    inline def newOutput[w <: Int: ValueOf](
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
