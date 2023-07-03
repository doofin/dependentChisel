package dependentChisel.typesAndSyntax

import scala.compiletime.ops.int.*
import scala.compiletime.*

import com.doofin.stdScalaJvm.*

// import dependentChisel.macros.getVarName
import dependentChisel.*
import dependentChisel.typesAndSyntax.chiselModules.*
import typesAndOps.*
import dependentChisel.codegen.seqCommands.*
import codegen.firrtlTypes.*
import dependentChisel.syntax.naming
import dependentChisel.misc.macros
import dependentChisel.misc.depTypes

/** assignments */
object statements {

  /** typed API for assign */
  extension [w <: Int, V <: Var[w]](v: V) {

    inline def :=(using mli: ModLocalInfo)(oth: Expr[w]) = {
      val name = v.getname

      /* v match {
        case VarLit(name) =>
        case Input(name)  =>
        case Output(name) =>
      } */
      // dbg(v)
      // dbg(oth)
      // mli.typeMap.addOne(v, constValueOpt[w].get)
      mli.commands += FirStmt(v, ":=", oth)
    }

  }

  /** untyped API for assign */
  extension (v: VarDymTyped) {
    inline def :=(using mli: ModLocalInfo)(oth: Expr[?]) = {
      val name = v.getname
      mli.typeMap.addOne(v, v.width)

      mli.commands += FirStmt(v, ":=", oth)
    }
  }
}

/*
 case class InputB[w <: Int, b <: BitsType[w]](x: b) extends ExprB[w, b] {}

 inline def newInput[w <: Int](using m: ModLocalInfo)(
      givenName: String = ""
  ) = {
    val name =
      s"${m.classNm}.${if givenName.isEmpty() then "i" else givenName}${naming.getIdWithDash}"
    // io : type,name,width
    m.io.prepend(IOdef(m.instNm, name, "input", constValueOpt[w]))
    Input[w](name)
  }

  varW <: Int
 */

/* inline def newOutput[w <: Int](using m: ModLocalInfo)(
      givenName: String = ""
  ) = {
    val name =
      s"${m.classNm}.${if givenName.isEmpty() then "i" else givenName}${naming.getIdWithDash}"

    m.io.prepend(IOdef(m.instNm, name, "output", constValueOpt[w]))
    Output[w](name)
  } */

// InputB(BitsType.Bits()) + InputB(BitsType.UInt()) // fail,ok
// InputB(BitsType.Bits()) + InputB(BitsType.Bits())
