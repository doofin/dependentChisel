import dependentChisel.macros // call macros here
import scala.quoted.*
import macros._
import com.doofin.stdScala.mainRunnable

object macroCall extends mainRunnable {

  override def main(args: Array[String]): Unit = run

  def run = {
    // print a class
    println("inspectClass: " + inspectClassTyped(new c1(2))._2)
  }

  class c1(param1: Int) {
    val v1 = 1
    def method1() = {}
    def method2() = { val v2 = v1 }
  }
  // repeat("hello", n) // ERROR!

}
