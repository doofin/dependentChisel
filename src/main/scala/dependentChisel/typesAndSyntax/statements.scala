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

object statements {
  enum Stmt {
    case StmtNoOp
  }

  // InputB(BitsType.Bits()) + InputB(BitsType.UInt()) // fail,ok
  // InputB(BitsType.Bits()) + InputB(BitsType.Bits())

  extension [w <: Int, modTp <: Var[w]](v: modTp) {

    inline def :=(using BlockLocalInfo)(oth: Expr[w]) = {
      val name = v.getname
      summon += s"${name} := ${oth},width=${constValueOpt[w]}"
      Stmt.StmtNoOp
    }

    inline def :==(using ModLocalInfo)(oth: Expr[w]) = {
      val name = v.getname
      summon.commands += s"${name} := ${oth},width=${constValueOpt[w]}"
      Stmt.StmtNoOp
    }

  }

  /** w:width */
  /* case class UIntDep[w <: Int]() extends Bits[w] {
    inline def valu = constValueOpt[w]
  } */

  case class Input[w <: Int](name: String) extends Var[w](name), Expr[w] {}

  case class InputB[w <: Int, b <: BitsType[w]](x: b) extends ExprB[w, b] {}

  inline def newInput[w <: Int](using m: ModLocalInfo, dp: DependenciesInfo)(
      givenName: String = ""
  ) = {
    val name = s"${m.classNm}.$givenName${dp.counter.getIdWithDash}"
    m.io.prepend(s"input ${name}[${constValueOpt[w]}]")
    Input[w](name)
  }

// varW <: Int

  case class Output[w <: Int](name: String = "")(using ModLocalInfo)
      extends Var[w](name),
        Expr[w] {}

  inline def newOutput[w <: Int](using m: ModLocalInfo, dp: DependenciesInfo)(
      givenName: String = ""
  ) = {
    val name = s"${m.classNm}.$givenName${dp.counter.getIdWithDash}"
    m.io.prepend(s"input ${name}[${constValueOpt[w]}]")
    Output[w](name)
  }
}
