package dependentChisel

/* global vars */
object global {
  var counter = 0
  def getUid = {
    counter += 1
    counter
  }
}
