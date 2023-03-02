package dependentChisel.syntax

object naming {
  class Counter() {
    var n: Int = 0
    def getIdWithDash = {
      val id = n
      n += 1
      "_" + n.toString()
    }
  }
}
