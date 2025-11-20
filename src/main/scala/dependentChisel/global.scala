package dependentChisel

/** global and package-wide settings
  *
  * same as package object in scala 2
  */
object global {
  val enableWidthCheck = true
  // val enableWidthCheck = false

  val debugVerbose = false
  var counter = 0
  // java.util.UUID.randomUUID.toString // System.currentTimeMillis().toString()
  def getUid = {
    counter += 1
    counter
  }

  /** my pprint, not show field names
    *
    * @param x
    */
  def mPPrint[T](x: T) = { pprint.pprintln(x, showFieldNames = false) }
}
