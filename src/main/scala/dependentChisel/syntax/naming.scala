package dependentChisel.syntax

object naming {
  class Counter() {
    var n: Int = 0
    def getIdWithDash = {
      "_" + getIntId
    }

    def getIntId = {
      n += 1
      n
    }
  }
}
