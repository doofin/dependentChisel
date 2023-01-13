// package precondition.syntax

import cats.implicits._
import cats.free.Free._
import cats.free.Free

object dslAST {

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
  case class wireTp[n <: Int]() extends DslStoreA[wireTp[n]]

  case class If(cond: BoolExpr, s1: DslStore[Unit], s2: DslStore[Unit]) extends DslStoreA[Unit]
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

  inline def newWire[n <: Int](name: String = ""): Free[DslStoreA, wireTp[n]] = liftF(wireTp[n]())

  inline def wireConn[n <: Int](x: wireTp[n], y: wireTp[n]) = {}
  def skip: Free[DslStoreA, Unit] = liftF(Skip)

  /** https://github.com/rtitle/free-control/blob/master/src/main/scala/control/free/ControlFlowInterpreter.scala
    */
  def while__[t](cond: DslStore[Boolean], annotation: String)(
      dslStoreA: DslStore[Unit]
  ) =
    liftF[DslStoreA, Unit](While(cond, annotation, dslStoreA))

  def if_(cond: BoolExpr, s1: DslStore[Unit], s2: DslStore[Unit]) =
    liftF[DslStoreA, Unit](If(cond, s1, s2))

  def liftBool = liftF[DslStoreA, Boolean] _
  def liftBoolEx = liftF[DslStoreA, BoolExpr] _
  def true_const = liftBool(True) // liftF[DslStoreA, Boolean](True)

  liftBoolEx(BoolExprWrp(BoolConst(true)))
}
