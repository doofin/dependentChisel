// package precondition.syntax
package dependentChisel.syntax

// import precondition._
import cats.data.*
import cats.implicits.*
import cats.free.Free.*
import cats.free.Free
import cats.{Id, ~>}
import compilers.*
// import precondition.syntax.dslAST
import com.doofin.stdScalaJvm.*
import monadicAST.*
import com.doofin.stdScala.mainRunnable
import cats.arrow.FunctionK
//import cuttingedge.progAnalysis.ast.Expr.Var
//import cuttingedge.progAnalysis.ast._
object monadicTest extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {
    // val compiler1 = new impureCompilerCls // compilerToStr
    // val r_notused = sgdProgram.foldMap(compiler1)
    // val resu = compiler1.kvs.toList.sortBy(_._1)
    // dbg(resu)
    // pp(resu)

    val compiler2 = new impureCompilerCls // compilerToStr
    depChisel1.foldMap(compiler2)
    // dbg(compiler2.getResu())

  }

  def depChisel1 = {
    for {
      in1 <- newVar("in1") // input
      in2 <- newVar("in2") // input
      out1 <- newVar("out1") // output
      w1 <- newWire[1]()
      w11 <- newWire[1]()
      w2 <- newWire[2]()

      // no effect below
      r = wireConn(w1, w11)
      r2: NewWire[2] = wireConcat(w1, w11)
      // r2 = wireConn(w1, w2) // err!
    } yield r // ok
  }

  /*   def adder1 = {
    for {
      a <- newIn[2]("a") // input
      b <- newIn[2]("b") // output
      y <- newOut[2]("y")
      y2 <- newOut[2]("y2")
      _ <- y := a
      _ <- assign(if (1 to 2).sum == 3 then y else y2, a)
      _ <- (if (1 to 2).sum == 1 then y else y2) := a
      // r2 = wireConn(w1, w2) // err!
    } yield ()
  } */
}

/* def sgdProgram: DslStore[monadicAST.Var] = {
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
} */
