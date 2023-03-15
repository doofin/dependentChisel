package dependentChisel.typesAndSyntax

import dependentChisel.*
import scala.compiletime.ops.int.*
import scala.compiletime.*

import com.doofin.stdScalaJvm.*
import syntax.ImperativeModules.*
import dependentChisel.macros.getVarName
import syntax.tree.*
import depTypes.*
import basicTypes.*

import dependentChisel.codegen.seqCmds.FirStmt
import codegen.firAST.*

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
    m.io.prepend(s"input ${name}[${constValueOpt[w]}]")
    Input[w](name)
  }

// varW <: Int

  inline def newOutput[w <: Int](using m: ModLocalInfo, dp: globalInfo)(
      givenName: String = ""
  ) = {
    val name = s"${m.classNm}.$givenName${dp.counter.getIdWithDash}"
    m.io.prepend(s"input ${name}[${constValueOpt[w]}]")
    Output[w](name)
  }
}
