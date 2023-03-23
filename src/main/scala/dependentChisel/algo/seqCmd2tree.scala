package dependentChisel.algo

import scala.util.*
import Tree.*
import com.doofin.stdScalaCross.*

import dependentChisel.codegen.seqCmdTypes.*

import dependentChisel.codegen.compiler.*
import dependentChisel.typesAndSyntax.basicTypes.*

import dependentChisel.codegen.seqCmdTypes

/** sequential commands to AST */
object seqCmd2tree {
  type AST = TreeNode[FirStmt | Ctrl]

  def cmdListToSingleAssign(cmdList: List[Cmds]): List[Cmds] = {
    cmdList flatMap {
      case x: FirStmt =>
        val fir = stmtToSingleAssign(x)
        // dbg(fir)
        fir
      case orig @ Start(ctrl, uid) =>
        val r: List[Cmds] = ctrl match {
          case ctrlIf @ Ctrl.If(b: BoolExpr[Int]) =>
            val anf_stmts = stmtToSingleAssign(expr2stmtBind(b))
            // dbg(anf)
            anf_stmts :+ orig.copy(ctrl =
              ctrlIf.copy(cond = anf_stmts.last.lhs)
            )
          // case Ctrl.Else()           =>
          // case Ctrl.Top()            =>
          case _ => List()
        }

        // List(x)
        r
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

}
