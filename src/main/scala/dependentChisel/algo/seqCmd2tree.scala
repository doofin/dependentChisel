package dependentChisel.algo

import scala.util.*

import Tree.*
import com.doofin.stdScalaCross.*

import dependentChisel.codegen.seqCommands.*

/** algorithm to convert sequential commands to AST */
object seqCmd2tree {
  type AST = TreeNode[NewInstStmt | FirStmt | Ctrl | VarDecls]

  /** convert sequential commands to AST. multiple stmt is appended as multiple nodes
    */
  def list2tree(cmdList: List[Cmds]): AST = {
    import scala.collection.mutable.Stack
    val parents: Stack[AST] = Stack(TreeNode(Ctrl.Top())) // new Stack[AST]
    // parents.push(TreeNode(Ctrl.Top()))
    // ppc(cmdList)

    cmdList.foreach { cmd =>
      // dbg(cmd)
      cmd match {
        case Start(ctrl, uid) =>
          /* start of block: create new node and append as child of curr top parent node if exists.
         then push new node into parent stack as new top elem*/
          val newParNode: AST = TreeNode(ctrl) // new parent node
          // add this newParNode as child
          parents.top.cld += newParNode
          parents push newParNode
        case End(ctrl, uid) =>
          // end of block, pop out one parent
          parents.pop()
        // for other stmt,just append
        case stmt: (FirStmt | NewInstStmt | VarDecls) =>
          val newNd: AST = TreeNode(stmt)
          parents.top.cld += newNd
        case _ =>
      }

      // println("parents after:"); ppc(parents)
    }

    parents.pop()
  }

}
