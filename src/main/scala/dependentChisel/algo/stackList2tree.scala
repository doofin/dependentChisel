package dependentChisel.algo

import scala.util.*
import scala.collection.mutable.Stack

import Tree.*
import com.doofin.stdScalaCross.*

import dependentChisel.codegen.sequentialCommands.*

/** algorithm to convert sequential commands to AST in tree structure
  */
object stackList2tree {
  type AST = TreeNode[NewInstance | WeakStmt | Ctrl | VarDecls]

  /** convert sequential commands to AST.
    *
    * @param cmdList
    *   list of sequential commands which implicitly has stack structure
    * @return
    *   AST tree structure where parent node has multiple children nodes
    */
  def list2tree(cmdList: List[Cmds]): AST = {
    val parents: Stack[AST] = Stack(TreeNode(Ctrl.Top())) // new Stack[AST]

    cmdList.foreach { cmd =>
      // dbg(cmd)
      cmd match {
        case Start(ctrl, uid) =>
          /* start of block: create new node and append as child of curr top parent node if exists.
         then push new node into parent stack as new top elem*/
          val newParNode: AST = TreeNode(ctrl) // new parent node
          // add this newParNode as child
          parents.top.children += newParNode
          parents push newParNode
        case End(ctrl, uid) =>
          // end of block, pop out one parent
          parents.pop()
        // for other stmt,just append
        case stmt: (WeakStmt | NewInstance | VarDecls) =>
          val newNd: AST = TreeNode(stmt)
          parents.top.children += newNd
        case _ =>
      }

      // println("parents after:"); ppc(parents)
    }

    parents.pop()
  }

}
