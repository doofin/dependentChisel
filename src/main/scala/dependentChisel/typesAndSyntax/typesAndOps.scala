package dependentChisel.typesAndSyntax

import scala.compiletime.ops.int.*
import scala.compiletime.*

import com.doofin.stdScalaJvm.*

import dependentChisel.*
import dependentChisel.macros.getVarName
// import dependentChisel.typesAndSyntax.chiselModules.*

/*
https://github.com/MaximeKjaer/tf-dotty/blob/master/modules/compiletime/src/main/scala/io/kjaer/compiletime/Shape.scala
 */
object typesAndOps extends exprOperators {

  /* Chisel provides three data types to describe connections, combinational logic, and
registers: Bits, UInt, and SInt. UInt and SInt extend Bits, and all three types
represent a vector of bits */

  /* mutable vars which can be mutated in lhs, incl input,output */
  sealed trait Var[w <: Int](name: String) extends Expr[w] {
    def getname = name
    // def getIsIO = isIO
  }

  case class VarLit[w <: Int](name: String) extends Var[w](name)

  sealed trait Expr[w <: Int] {
    def asUnTyped = this.asInstanceOf[Expr[Nothing]]
    inline def asTypedUnsafe[w <: Int] = {
      this.asInstanceOf[Expr[w]]
    }
  }
  case class BinOp[w <: Int](a: Expr[w], b: Expr[w], nm: String) extends Expr[w]

  // to prevent overflow like AFix in spinalHDL
  // case class MulOp[w <: Int](a: Expr[w], b: Expr[w], nm: String = "mul")
  //     extends Expr[2 * w]

  /** uniary op like negate */
  case class UniOp[w <: Int](a: Expr[w], nm: String) extends Expr[w]

  type Bool = Expr[1] // bool is just uint[1]
  // sealed trait BoolExpr[w <: Int] extends Expr[w]
  // case class ExprAsBool[w <: Int](expr: Expr[w]) extends BoolExpr[w]

  /** Wire, Reg, and IO */
  sealed trait VarType
  object VarType {
    case object Input extends VarType
    case object Output extends VarType
    case object Reg extends VarType
    case object Wire extends VarType
    case class RegInit(init: LitDym) extends VarType
  }

  sealed trait ExprC[w <: Int, tp <: VarType] {

    inline def +(oth: ExprC[w, tp]) = {}
  }

  // new ExprC[1, VarDeclTp.Reg.type] {} + new ExprC[1, VarDeclTp.Wire.type] {} //ok ,will fail

  /** untyped API for Wire, Reg, and IO */
  case class VarDymTyped(width: Int, tp: VarType, name: String)
      extends Var[Nothing](name) {

    /** dym check for type cast */
    inline def asTyped[w <: Int] = {

      constValueOpt[w].foreach { wd =>
        val castOk = wd == width
        val msg = s"asTyped cast error in ${this}!"
        if (!castOk) {
          println(msg)
        }
        assert(castOk, msg)
      }

      this.asInstanceOf[Var[w]]
    }
  }

  /** typed API for Wire, Reg, and IO */
  case class VarTyped[w <: Int](name: String, tp: VarType) extends Var[w](name) {

    /** a dirty hack */
    def toDym(width: Int) = VarDymTyped(width, tp, name)
  }

  // case class Input[w <: Int](name: String) extends Var[w](name)

  // case class Output[w <: Int](name: String) extends Var[w](name)

  // for future use

// case class Lit[w <: Int](i: w, name: String) extends Expr[w] {}
  type sml[w <: Int] <: w ^ 2
  case class Lit[w <: Int](i: w) extends Expr[w] {}
  case class LitDym(i: Int, width: Int) extends Expr[Nothing]
  /*
  extension [w <: Int](i: Bounded[0, w]) {
    def asLit = toLit[w](i)
  }
   */
}
