package dependentChisel.syntax

import dependentChisel.*
import dataTypes.*
import com.doofin.stdScala.mainRunnable
import dependentChisel.syntax.ImperativeModules.ModCircuits

object functional extends mainRunnable {

  override def main(args: Array[String]): Unit = {}

  /* can't access returned module if defined as function?
  restrict : differentiate between expr and stmt?
  for stmt,use monad or vars
  y=a+b+c+d
   */
  def adder1[w <: Int](a: Input[w], b: Input[w]) = { a + b }
//   def adder2[w <: Int](a: Input[w], b: Input[w]) = { y :== a + b }
  def adder[w <: Int](a: Input[w], b: Input[w], c: Input[w], d: Input[w], y: Output[w]) = {
    y :== adder1(a, b) + adder1(c, d)
    if Seq(1, 2).sum == 1
    then y :== adder1(a, b)
    else y :== adder1(a, b) + adder1(c, d)
  }

  def genCircuit()(using ModCircuits) = {
    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val c = newInput[2]("a")
    val d = newInput[2]("b")

    val y = newOutput[2]("y")
    adder(a, b, c, d, y)
  }
}
