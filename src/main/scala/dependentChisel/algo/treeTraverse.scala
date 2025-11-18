package dependentChisel.algo

import scala.collection.mutable.ArrayBuffer

/** tree traversal algorithms
  */
object treeTraverse {

  /** imperitive pre-order traversal
    *
    * @param node
    * @param visit
    */
  def preOrder[t](visit: t => Unit, node: Tree.TreeNode[t]): Unit = {
    visit(node.value)
    node.children.foreach(preOrder(visit, _))
  }

  /** filter tree nodes based on predicate. There are multiple ways to interpret this:
    *
    * 1 We remove only the leaves that do not satisfy the predicate, and for branch nodes, we keep
    * them if either they satisfy the predicate or if they have any descendant that satisfies the
    * predicate (so we keep the structure to reach the satisfying leaves).
    *
    * 2 We remove every node that does not satisfy the predicate, and if a branch node is removed,
    * then we must somehow promote its children? But then the tree structure might change.
    *
    * 3 We do a transformation such that we keep the tree structure, but we remove any leaf that
    * doesn't satisfy the predicate, and for branch nodes, we keep them only if they satisfy the
    * predicate OR if they have at least one child that is kept. This is similar to a prune.
    *
    * TODO: test this function
    * @param predicate
    * @param node
    * @return
    *   tree with nodes filtered
    */
  def filter[t](
      predicate: t => Boolean,
      node: Tree.TreeNode[t]
  ): Tree.TreeNode[t] = {
    val flag @ (yes, hasChild) = (predicate(node.value), node.children.nonEmpty)

    // if children is empty, it's leaf node, just return itself if satisfies predicate
    if hasChild then {
      // recursively filter its children
      val filteredChildren = node.children
        .map(child => filter(predicate, child))
        .filter(child => predicate(child.value) || child.children.nonEmpty)
      Tree.TreeNode(node.value, filteredChildren)
    } else {
      // leaf node, just return itself if satisfies predicate
      if yes then node else Tree.TreeNode(node.value, ArrayBuffer())
    }

  }
}
