// call macros here
import scala.quoted.*
import macros._
object macroCall {

  def run = {
    println(inspect(new c1(2)))
    println("inspectTyped: " + inspectTyped(new c1(2)))
  }

  class c1(p1: Int) {
    val n1 = 1
    def m1() = {}
  }
  // repeat("hello", n) // ERROR!

}
