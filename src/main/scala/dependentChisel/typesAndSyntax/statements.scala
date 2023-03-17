package dependentChisel.typesAndSyntax

import dependentChisel.*
import scala.compiletime.ops.int.*
import scala.compiletime.*

import com.doofin.stdScalaJvm.*
import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.macros.getVarName
// import syntax.tree.*
import depTypes.*
import basicTypes.*

import dependentChisel.codegen.seqCmds.FirStmt
// import codegen.Compiler.*
import codegen.firrtlTypes.*

object statements {

  // InputB(BitsType.Bits()) + InputB(BitsType.UInt()) // fail,ok
  // InputB(BitsType.Bits()) + InputB(BitsType.Bits())

  extension [w <: Int, V <: Var[w]](v: V) {

    inline def :=(using ml: ModLocalInfo)(oth: Expr[w]) = {
      val name = v.getname
      ml.commands += FirStmt(v, ":=", oth)
    }

  }

  case class InputB[w <: Int, b <: BitsType[w]](x: b) extends ExprB[w, b] {}

  inline def newInput[w <: Int](using m: ModLocalInfo, dp: globalInfo)(
      givenName: String = ""
  ) = {
    val name = s"${m.classNm}.$givenName${dp.counter.getIdWithDash}"
    // io : type,name,width
    m.io.prepend(IOdef(name, "input", constValueOpt[w]))
    Input[w](name)
  }

// varW <: Int

  inline def newOutput[w <: Int](using m: ModLocalInfo, dp: globalInfo)(
      givenName: String = ""
  ) = {
    val name = s"${m.classNm}.$givenName${dp.counter.getIdWithDash}"
    m.io.prepend(IOdef(name, "output", constValueOpt[w]))
    Output[w](name)
  }

}
