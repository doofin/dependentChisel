// package precondition.syntax

// import precondition._
import cats.data._
import cats.implicits._
import cats.free.Free._
import cats.free.Free
import cats.{Id, ~>}
import compilers._
// import precondition.syntax.dslAST
import com.doofin.stdScalaJvm.*
import dslAST._
import com.doofin.stdScala.mainRunnable
import cats.arrow.FunctionK

//import cuttingedge.progAnalysis.ast.Expr.Var
//import cuttingedge.progAnalysis.ast._
object dslTest extends mainRunnable {

  override def main(args: Array[String]): Unit = testDsl

  type vals = Int
//  type St   = Map[Expr, vals]
//  type Epi  = (St, St) => Float

  def testDsl = {
    val compilerId = new impureCompilerCls // compilerToStr
    // val compilerId = compilerToHvl
    val r_notused = sgdProgram.foldMap(compilerId)
    // compilerId.
//    println(rImpure)
    val resu = compilerId.kvs.toList.sortBy(_._1)
    // dbg(resu)
    pp(resu)

  }

  val annos = "invariant"
  def depChis = {
    for {
      w1 <- newWire[1]()
      w2 <- newWire[1]()
      w3 <- newWire[2]()
    } yield (wireConn(w1, w2)) // ok
  }

  def sgdProgram: DslStore[dslAST.Var] = {
    for {
      w <- newVar("w")
      _ <- varAssign(w, LitNum(1))
      t <- newVar("t")
      _ <- while__(true__, annos) {
        for {
          s <- newVar("s")
          _ <- varAssign(s, LitNum(1))
          _ <- varAssign(w, w)
        } yield s
      }
      _ <- skip
    } yield { t }
  }

}
