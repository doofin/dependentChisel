package dependentChisel.algo

import scala.collection.mutable.ArrayBuffer

object Tree {
  case class TreeNode[t](
      val value: t,
      val cld: ArrayBuffer[TreeNode[t]] = ArrayBuffer[TreeNode[t]]()
  )
}
