package dependentChisel.algo
import dependentChisel.codegen.seqCmds.*
import dependentChisel.*

import Tree.*
import scala.util.*
import codegen.firAST.*
import dependentChisel.typesAndSyntax.basicTypes.Bool

object seqCmd2tree {

  type AST = TreeNode[FirStmt | Ctrl]

  def cmd2ANF(cmdList: List[Cmds]): List[Cmds] = {
    cmdList flatMap {
      case x: FirStmt => toANF(x)
      case x @ Start(ctrl, uid) =>
        val r: List[Cmds] = ctrl match {
          case Ctrl.If(b: Bool[Int]) => toANF(expr2stmtBind(b))
          // case Ctrl.Else()           =>
          // case Ctrl.Top()            =>
          case _ => List()
        }

        // List(x)
        r :+ x
      case x => List(x)
    }
  }

  def list2tree(cmdList: List[Cmds]): AST = {
    import scala.collection.mutable.Stack
    val parents: Stack[AST] = Stack(TreeNode(Ctrl.Top())) // new Stack[AST]
    // parents.push(TreeNode(Ctrl.Top()))

    cmdList.foreach { cmd =>
      cmd match {
        case Start(ctrl, uid) => // start of block
          /* create new node and append as child of curr top parent node if exists
         push new node into parent stack*/
          val newNd: AST = TreeNode(ctrl)
          Try(parents.top).foreach(p => p.cld += newNd)
          parents push newNd
        case End(ctrl, uid) =>
          // end of block, pop out one parent
          parents.pop()
        case stmt @ FirStmt(lhs, op, rhs, prefix) =>
          val newNd: AST = TreeNode(stmt)
          Try(parents.top).foreach(p => p.cld += newNd)
      }
    }
    parents.pop() // .cld.toList
  }

  def tree2str(tr: AST, indent: String = ""): String = {
    val valStr: String = tr.value match {
      case x: Ctrl =>
        x match {
          case Ctrl.If(b)  => "when " + b.expr.toString()
          case Ctrl.Else() => "else "
          case Ctrl.Top()  => ""
        }
      case stmt @ FirStmt(lhs, op, rhs, prefix) => stmt.toString()
    }

    indent + valStr + (tr.cld map (cld =>
      "\n" + tree2str(cld, indent + "  ")
    )).mkString // ("\n")
  }
}
