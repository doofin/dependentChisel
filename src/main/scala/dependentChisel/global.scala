package dependentChisel

/* global vars */
object global {
  val enableWidthCheck = false
  var counter = 0
  // java.util.UUID.randomUUID.toString // System.currentTimeMillis().toString()
  def getUid = {
    counter += 1
    counter
  }
}
