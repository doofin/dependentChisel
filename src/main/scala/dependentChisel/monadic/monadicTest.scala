// package precondition.syntax
package dependentChisel.monadic

// import precondition._
import cats.data.*
import cats.implicits.*
import cats.free.Free.*
import com.doofin.stdScalaJvm.*
import com.doofin.stdScala.mainRunnable

import monadicCompilers.*
import monadicAST.*
object monadicTest extends mainRunnable {

  override def main(args: Array[String] = Array()): Unit = {

    val compilerMut = new compilerToSeqCmd // compilerToStr
    depChisel3.foldMap(compilerMut)
    // dbg(compiler2.getResu())
    val seqCmd = compilerMut.stmtsMut.toList
    val ast = list2tree(seqCmd)
    pp(seqCmd)
    pp(ast)

  }

  def depChisel2 = {
    for {
      a <- newIn("a") // input
      b <- newIn("b") // input
      y <- newOut("y") // output
      y2 <- newOut("y2") // output
      _ <- if_(
        BoolConst(true),
        for {
          _ <- y := a + b
          _ <- y2 := a - b
        } yield ()
      )
    } yield ()
  }

  /* err! compiler with one flag doesn't work with more embed level  */
  def depChisel3 = {
    for {
      a <- newIn("a") // input
      b <- newIn("b") // input
      y <- newOut("y") // output
      y2 <- newOut("y2") // output
      _ <- if_(
        BoolConst(true),
        for {
          _ <- y := a + b
          _ <- if_(
            BoolConst(true),
            for {
              _ <- y2 := a - b
            } yield ()
          )
        } yield ()
      )
    } yield ()
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

}
