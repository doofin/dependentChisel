// package precondition.syntax
package dependentChisel.monadic

import cats.{Id, ~>}
import cats.data.State

import monadicAST.*
import simpleAST.*
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import dependentChisel.algo.Tree.TreeNode

object monadicCompilers {

  /** natural transformation(pseudo,since it's effectful) between type containers. need
    * two lang,dsl->ast, can also translate into tree
    */
  class compilerToSeqCmd extends (DslStoreA ~> Id) {
    // var ln: Int = 0 // line number, not used yet
    var stmtsMut: ArrayBuffer[Stmt] = ArrayBuffer() // store stmts as mutable list
    override def apply[A](fa: DslStoreA[A]): Id[A] = {

      // ln += 1
      fa match {
        case x: Input[n] =>
          stmtsMut += Stmt.Decl(x.toString())
          x

        case x: Output[n] =>
          stmtsMut += Stmt.Decl(x.toString())
          x

        case x: Assign[n] =>
          stmtsMut += Stmt.Assign(x.toString())
          x
        case x: BinOp[n]        => x
        case x @ If(cond, body) =>
          // indicate that code block starts
          stmtsMut += Stmt.Start(Stmt.If(cond))
          val r = body.foldMap(this) // jump to execute body
          stmtsMut += Stmt.End(Stmt.If(cond))
          r

        case VarAssign(v, value) =>
          ()
        case NewVar(name) => Var(name)
        case While(cond, annotation, body) =>
          println("While start")
          //            use some state there
          val r = body.foldMap(this)
          println("While end")
          r
        case True => true
        case Skip =>

        case x =>
          println(x)
          ???
      }
    }
  }

  import Stmt.*
  type AST = TreeNode[Stmt]

  /** convert sequential commands to AST. multiple stmt is appended as multiple nodes
    */
  def list2tree(cmdList: List[Stmt]): AST = {
    import scala.collection.mutable.Stack
    val parents: Stack[AST] = Stack(TreeNode(Stmt.Top())) // new Stack[AST]

    cmdList.foreach { cmd =>
      cmd match {
        case Start(ctrl) =>
          /* start of block: create new node and append as child of curr top parent node if exists.
         then push new node into parent stack as new top elem*/
          val newParNode: AST = TreeNode(ctrl) // new parent node
          // add this newParNode as child
          parents.top.cld += newParNode
          parents push newParNode
        case End(ctrl) =>
          // end of block, pop out one parent
          parents.pop()
        // for other stmt,just append
        case stmt =>
          parents.top.cld += TreeNode(stmt)
      }

    }

    parents.pop()
  }

  /* dsl -> state monad */
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
