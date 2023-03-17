package dependentChisel.misc

import dependentChisel.syntax.*
import monadicCompilers.*
import monadicAST.*
// import precondition.syntax.dslAST
import com.doofin.stdScalaJvm.*
import com.doofin.stdScala.mainRunnable
import cats.free.Free

import naming.*

object adderMonadic extends mainRunnable {

  override def main(args: Array[String]): Unit = {

    println("\n adder:")
    given Counter = Counter()
    val compiler1 = new impureCompilerCls // compilerToStr
    adder.foldMap(compiler1)

    println("\n doubleAdder:")
    val compiler4 = new impureCompilerCls // compilerToStr
    doubleAdder.foldMap(compiler4)

    println("\n adderDynamic:")
    val compiler5 = new impureCompilerCls // compilerToStr
    adderDynamic.foldMap(compiler5)

  }

  def adder(using Counter): Free[DslStoreA, (Input[2], Input[2], Output[2])] = {

    for {
      a <- newIn[2]("a") // input
//     val  a = newIn[2]("a") // input
// Seq(a,b,c)

      // _ <- (1 to n) loop {i=>connect(wire(i),a)}
      b <- newIn[2]("b") // output
      y <- newOut[2]("y")
      // w<- newWire
      _ <- y := a + b
    } yield (a, b, y) // also return wire
  }

  def doubleAdder = {
    given Counter = Counter()

    for {
      // w<-newWire
      a1 <- newIn[2]("a") // input
      b1 <- newIn[2]("b") // output

      (a, b, y1) <- adder
      // w:=a
      //   _ <- a := 1
      (c, d, y2) <- adder
      _ <- c := y1
      //   w:=c
      y <- newOut[2]("y")
      _ <- a1 := a
      _ <- y := y1 + y2
    } yield ()
  }

  def somethingDynamic: Boolean = (1 to 2).sum == 1
  /*  */
  def adderDynamic = {
    given Counter = Counter()
    for {
      a <- newIn[2]("a") // input
      b <- newIn[2]("b") // output
      y1 <- newOut[2]("y1")
      y2 <- newOut[2]("y2")
      _ <- y1 := a + b
      //   _ <- (if somethingDynamic then y1 else y2) := a
      _ <- (if !somethingDynamic then y1 else y2) := a
    } yield ()
  }
}
/*
val a=Input()
val b=Input()

def adder[N](a: Input[N], b: Input[N], y: Output[N]) =
  Module("Adder") {
    y := a + b
  }

def myModule(out: Output) =
  Module("MyModule") {
    val one = One("one")
    val zero = Zero("zero")
    val w = Wire("w")

//modules
    [adder(one, zero, w),
     adder(one, w, out)]
  }
  // returns out => BunchOfModulues([
  //   Module("Adder", Assign(Wire("w"), Sum(One("one"), Zero("zero"))))
  //   Module("Adder", Assign(out, Sum(One("one"), Wire("w"))))
     ])
 */
/*

enum Module =
   case BunchOfModules(mods: List[Module])
   case Assign(target: Module, source: Module)
   case Sum(lhs: Module, rhs: Module)
   case Wire[N <: int](size: N)
   case One
   case Zero
 */
