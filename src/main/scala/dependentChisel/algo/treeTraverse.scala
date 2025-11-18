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

  /** filter tree nodes based on predicate
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
