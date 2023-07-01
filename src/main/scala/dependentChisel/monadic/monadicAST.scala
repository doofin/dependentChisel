// package precondition.syntax
package dependentChisel.monadic

import scala.compiletime.*
import scala.compiletime.ops.int.*

import cats.implicits.*
import cats.free.Free.*
import cats.free.Free

import dependentChisel.syntax.naming.*
import dependentChisel.syntax.naming

object monadicAST {

  sealed trait DslStoreA[A]

  sealed trait Expr1
  case class Var(nm: String = "") extends Expr1
  case class LitNum(num: Int) extends Expr1

  // case class BinOpVal(v1: Expr1, op: String, v2: Expr1) extends Expr1

  sealed trait BoolExpr
  // case class BinOpBool(v1: Expr1, op: String, v2: Expr1) extends BoolExpr
  case class BoolConst(b: Boolean) extends BoolExpr
  case class BoolExprWrp(b: BoolExpr) extends DslStoreA[BoolExpr]

  case class VarAssign[T](v: Var, value: Expr1) extends DslStoreA[Unit]

  case class NewVar(name: String = "") extends DslStoreA[Var] // DslStoreA[Var]
  // case class NewWire[t](name: String = "") extends DslStoreA[Var] // DslStoreA[Var]
  case class NewWire[n <: Int]() extends DslStoreA[NewWire[n]] { // support both dynamic and static check
    inline def getVal = constValueOpt[n]
  }

  sealed trait Vars[n <: Int](name: String)
  sealed trait Exprs[n <: Int]

  case class BinOp[w <: Int](a: Exprs[w], b: Exprs[w], opName: String)
      extends DslStoreA[Exprs[w]],
        Exprs[w] {
    override def toString(): String = s"${a} ${opName} ${b}"
  }
  /* case class BinOp[n <: Int](name: String = "") extends DslStoreA[In[n]], Vars[n](name), Exprs[n](name) { // support both dynamic and static check
    inline def getVal = constValueOpt[n]
  } */

  case class Input[n <: Int](name: String = "")
      extends DslStoreA[Input[n]],
        Vars[n](name),
        Exprs[n] { // support both dynamic and static check
    inline def getVal = constValueOpt[n]
  }

  case class Output[n <: Int](name: String = "")
      extends DslStoreA[Output[n]],
        Vars[n](name),
        Exprs[n] { // support both dynamic and static check
    inline def getVal = constValueOpt[n]
  }
  case class Assign[n <: Int](x: Vars[n], y: Exprs[n]) extends DslStoreA[Assign[n]] {
    override def toString(): String = s"${x} := ${y}"
  }

  inline def wireConcat[n <: Int, m <: Int](
      x: NewWire[n],
      y: NewWire[m]
  ): NewWire[n + m] = {
    NewWire[n + m]()
  }
  case class IfElse(cond: BoolExpr, s1: DslStore[Unit], s2: DslStore[Unit])
      extends DslStoreA[Unit]
  case class If(cond: BoolExpr, s1: DslStore[Unit]) extends DslStoreA[Unit]
  case class While(
      cond: DslStore[Boolean],
      annotation: String,
      body: DslStore[Unit]
  ) extends DslStoreA[Unit]

  case object True extends DslStoreA[Boolean] // only give value while write compilers!

  case object Skip extends DslStoreA[Unit]

  type DslStore[A] = Free[DslStoreA, A]

  def whileM_[A](cond: DslStore[Boolean], prog: DslStore[A]): DslStore[Unit] =
    cond.flatMap {
      case false => ().pure[DslStore]
      case true  => prog >> whileM_(cond, prog)
    }

  def varAssign[T](v: Var, value: Expr1): DslStore[Unit] =
    liftF[DslStoreA, Unit](VarAssign[T](v, value))

  import scala.compiletime.ops.int.*
  import scala.compiletime.ops.int.S

  def newVar(name: String = "") = liftF(NewVar(name))

  inline def newWire[n <: Int](name: String = ""): Free[DslStoreA, NewWire[n]] =
    liftF(NewWire[n]())

  inline def newIn[n <: Int](name: String = "") = liftF(
    Input[n](name + naming.getIdWithDash)
  )
  inline def newOut[n <: Int](name: String = "") = liftF(
    Output[n](name + naming.getIdWithDash)
  )

  inline def assign[n <: Int](x: Vars[n], y: Exprs[n]) = liftF(Assign(x, y))

  extension [n <: Int](x: Vars[n]) {
    inline def :=(y: Exprs[n]) = assign(x, y)
  }

  extension [n <: Int](x: Exprs[n]) {

    def +(y: Exprs[n]) = BinOp(x, y, "+") // liftF(BinOp(x, y, "+"))
    def -(y: Exprs[n]) = BinOp(x, y, "-") // liftF(BinOp(x, y, "+"))
  }

  inline def wireConn[n <: Int](x: NewWire[n], y: NewWire[n]) = {}

  inline def wireConnOpt[n <: Int](
      x: Option[NewWire[n]],
      y: Option[NewWire[n]]
  ) = {}

  def skip: Free[DslStoreA, Unit] = liftF(Skip)

  /** https://github.com/rtitle/free-control/blob/master/src/main/scala/control/free/ControlFlowInterpreter.scala
    */
  def while__[t](cond: DslStore[Boolean], annotation: String)(
      dslStoreA: DslStore[Unit]
  ) =
    liftF[DslStoreA, Unit](While(cond, annotation, dslStoreA))

  def ifElse(cond: BoolExpr, s1: DslStore[Unit], s2: DslStore[Unit]) =
    liftF[DslStoreA, Unit](IfElse(cond, s1, s2))

  def if_(cond: BoolExpr, s1: DslStore[Unit]) =
    liftF[DslStoreA, Unit](If(cond, s1))

  def liftBool = liftF[DslStoreA, Boolean]
  def liftBoolEx = liftF[DslStoreA, BoolExpr]
  def true_const = liftBool(True) // liftF[DslStoreA, Boolean](True)

  liftBoolEx(BoolExprWrp(BoolConst(true)))
}
