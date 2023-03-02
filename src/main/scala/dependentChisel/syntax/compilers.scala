// package precondition.syntax
package dependentChisel.syntax

import cats.{Id, ~>}
import cats.data.State

import monadicAST.*
// import precondition.sgdExampleTup._

import scala.collection.mutable

// import precondition.syntax.smtAST._

object compilers {
  class impureCompilerCls extends (DslStoreA ~> Id) {
    //      val kvs                     = mutable.Map.empty[String, Any]
    val kvs = mutable.Map.empty[Int, String]
    //      tr : current node to insert
    var currCtx: Option[String] = None
    var ln: Int = 0
    def getResu() = kvs.toList.sortBy(_._1)
    override def apply[A](fa: DslStoreA[A]): Id[A] = {

      println(s"stmt : ${fa}")
      currCtx match {
        case Some(str) => kvs.put(ln, s"($str)  ${fa.toString}")
        case None      => kvs.put(ln, fa.toString)
      }

      ln += 1
      fa match {
        // case NewWire(name) => ()
        case x @ NewWire() =>
          // x.type
          x
        case VarAssign(v, value) =>
          ()
        case NewVar(name) => Var(name)
        case While(cond, annotation, body) =>
          println("While start")
          currCtx = Some(annotation)
          //            use some state there
          val r = body.foldMap(this)
          println("While end")
          currCtx = None
          r
        case True => true
        case Skip =>

        case x: Input[n] =>
          // println(x.toString() + ":" + x.getVal) // can't get int
          x

        case x: Output[n]    => x
        case x: Assign[n] =>
          // println("Assign:" + x.x.name)
          // Assign[n](x.x, x.y)
          x
        case x: BinOp[n] => x
        case x =>
          println(x)
          ???
      }
    }
  }

  (new impureCompilerCls).kvs

  /** natural transformation between type containers. need two lang,dsl->ast, can also translate into tree
    */

  /** read values after invoke natural transformation between type containers. need two lang,dsl->ast, can
    * also translate into tree
    */
  def compilerToStr: DslStoreA ~> Id =
    new (DslStoreA ~> Id) {
      //      val kvs                     = mutable.Map.empty[String, Any]
      val kvs = mutable.Map.empty[Int, String]
      //      tr : current node to insert
      var currCtx: Option[String] = None
      var ln: Int = 0
      override def apply[A](fa: DslStoreA[A]): Id[A] = {

        println(s"fa : ${fa}")
        currCtx match {
          case Some(str) => kvs.put(ln, s"($str)  ${fa.toString}")
          case None      => kvs.put(ln, fa.toString)
        }

        ln += 1
        fa match {
          case VarAssign(v, value) =>
            ()
          case NewVar(name) => Var(name)
          case While(cond, annotation, body) =>
            println("While start")
            currCtx = Some(annotation)
            //            use some state there
            val r = body.foldMap(this)
            println("While end")
            currCtx = None
            r
          case True => true
          case Skip => ()
          case _    => ???
        }
      }
    }
  // compilerToStr.kvs

  type StState[A] = State[Map[String, Any], A]
  val pureCompilerSt: DslStoreA ~> StState = new (DslStoreA ~> StState) {
    override def apply[A](fa: DslStoreA[A]): StState[A] = {
      fa match {
        case VarAssign(v, value) =>
          val r: StState[Unit] = State.modify(_.updated("", v))
          r
        case NewVar(name) =>
          val r: StState[Var] =
            State.inspect(_.getOrElse("", Var()).asInstanceOf[Var])
          r
        case While(cond, annotation, dslStoreA) =>
          val r = dslStoreA.foldMap(this)
          r
        case True =>
          val r: StState[Boolean] =
            State.inspect(_.getOrElse("", true).asInstanceOf[Boolean])
          r
        case _ => ???
      }
    }
  }

}

/* def compilerToHvl =
    new (DslStoreA ~> Id) {
      //      val kvs                     = mutable.Map.empty[String, Any]
      val kvs = mutable.Map.empty[Int, String]
      //      tr : current node to insert
      var currCtx: Option[String] = None
      var lineNum: Int = 0
      override def apply[A](fa: DslStoreA[A]): Id[A] = {
        lineNum += 1
        val dslStmt = fa

        val dslStr = dsl2hvl(dslStmt)
        currCtx match {
          case Some(str) => kvs.put(lineNum, s"($str)  ${dslStr}")
          case None      => kvs.put(lineNum, dslStr)
        }

        dslStmt match {
          case VarAssign(v, value) =>
            ()
          case NewVar(name)                  => Var(name)
          case While(cond, annotation, body) =>
            // println("While start")
            currCtx = Some(annotation)
            //            use some state there
            val r = body.foldMap(this)
            // println("While end")
            //            While(cond, annotation, bd.step)
            currCtx = None
            r
          case True             => true
          case Skip             => ()
          case If(cond, s1, s2) =>
        }
      }
    } */
