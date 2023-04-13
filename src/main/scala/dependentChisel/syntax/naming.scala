package dependentChisel.syntax

object naming {
  var n: Int = 0
  def genNameIfEmpty(givenName: String, prefix: String) = {
    s"${if givenName.isEmpty() then prefix else givenName}${naming.getIdWithDash}"
  }

  def getIdWithDash = {
    "_" + getIntId
  }

  def getIntId = {
    n += 1
    n
  }

  def mkUidFrom(s: String) = s + getIdWithDash

  // class Counter() {}
}
