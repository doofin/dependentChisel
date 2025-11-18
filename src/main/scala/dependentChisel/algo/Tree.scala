package dependentChisel.algo

import scala.collection.mutable.ArrayBuffer

object Tree {
  case class TreeNode[t](
      val value: t,
      val children: ArrayBuffer[TreeNode[t]] = ArrayBuffer[TreeNode[t]]()
  )
}
