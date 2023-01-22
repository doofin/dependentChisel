// package precondition.syntax
package dependentChisel.syntax

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

  override def main(args: Array[String]): Unit = {
    // val compiler1 = new impureCompilerCls // compilerToStr
    // val r_notused = sgdProgram.foldMap(compiler1)
    // val resu = compiler1.kvs.toList.sortBy(_._1)
    // dbg(resu)
    // pp(resu)

    val compiler2 = new impureCompilerCls // compilerToStr
    depChisel1.foldMap(compiler2)
    dbg(compiler2.getResu())

  }

  def depChisel1 = {
    for {
      in1 <- newVar("in1") // input
      out1 <- newVar("out1") // output
      w1 <- newWire[1]()
      w11 <- newWire[1]()
      w2 <- newWire[2]()
      r = wireConn(w1, w11)
      // r2 = wireConn(w1, w2) // err!
    } yield r // ok
  }

  def sgdProgram: DslStore[dslAST.Var] = {
    for {
      w <- newVar("w")
      _ <- varAssign(w, LitNum(1))
      t <- newVar("t")
      _ <- while__(true_const, "invariant") {
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
