package dependentChisel.syntax

import scala.collection.mutable.ArrayBuffer
import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.all.*
import dependentChisel.codegen.firAST.Cmds

/*inspired by scaloid
view:Elem
viewGroup:Container
 */
object tree extends mainRunnable {
  sealed trait Tree[a]
  case class Node[a](v: a, children: List[Tree[a]]) extends Tree[a]
  case class Leaf[a](v: a) extends Tree[a]
  override def main(args: Array[String]): Unit = run

  type ChildrenList = ArrayBuffer[Item]
  type BlockLocalInfo = ArrayBuffer[Cmds]

  sealed trait ItemOrBox

  sealed trait Item(nm: String) {
    given BlockLocalInfo = ArrayBuffer()
    def here(using pr: ChildrenList) = {
      pr += this
      this
    }
  }

  class Box(val nm: String) extends Item(nm) {
    given ChildrenList = ArrayBuffer()
  }

  class TopLevelCircuit extends Box("TopLevel") {
    val pr = summon[ChildrenList]

  }
  class When(override val nm: String)(using cl: ChildrenList)
      extends Box(nm + "_when") {

    def here = {
      cl += this
      this
    }
  }

  case class If[w <: Int](bool: Bool[w])(using cl: ChildrenList)
      extends Box("If") {

    cl += this

  }

  case class IfElse[w <: Int](bool: Bool[w])(elseBranch: List[Stmt])(using
      cl: ChildrenList
  ) extends Box("If") {

    cl += this

  }
  // new Class1{member1}
  // new Class2({abc})

  case class Else()(using pr: ChildrenList) extends Box("Else") {

    pr += this

  }

  def run = {
    val tp = new TopLevelCircuit {
      new When("w1") {
        new When("w1-1") {}.here
        new When("w1-2") {}.here
        new When("w2") {
          new When("w2-1") {}.here
          new When("w2-2") {}.here
        }.here
      }.here
    }

    pp(tp2tr(tp.pr.toList))

  }
  def tp2tr(arr: List[Item]): List[Tree[(String, String)]] = {
    arr.collect { case x: Box =>
      val circ = x.given_BlockLocalInfo.mkString("\n")
      Node(x.nm -> s"statements :\n$circ", tp2tr(x.given_ChildrenList.toList))
    }

  }
}
