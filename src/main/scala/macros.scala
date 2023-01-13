/*  Quoting and splicing are combined with inline to cause this macro implementation
to do compile-time metaprogramming.
 */
import pprint.*
import scala.quoted.* // imports Quotes, Expr

object macros {
  type PlusTwo[T <: Int] = scala.compiletime.ops.int.+[2, T]

  inline def repeat(s: String, count: Int): String =
    if count == 0 then ""
    else s + repeat(s, count - 1)

  inline def doSomething(inline mode: Boolean): String =
    if mode then "" else ""

  def run = {
    val a: PlusTwo[4] = 6
    doSomething(true)
    repeat("hello", 3) // Okay

    var n = 3
  }

  class c1(p1: Int) {
    val n1 = 1
  }
  // repeat("hello", n) // ERROR!

  /* Quotation and Splicing
  Quotation converts a code expression into a typed AST: T=> Expr[T]
  '{…}

  Splicing converts ast Expr[T] to an expression of type T
  ${…}
   */

  /* prints the expression of the provided argument at compile-time
  this executes as normal code
   */
  def inspectSimple1(x: Expr[Any])(using Quotes): Expr[Any] = {
    import quotes.reflect.*
    println(x.show) // ok
    println("inspectSimple1")
    // x.asTer
    val tm = x.asTerm
    tm.show(using Printer.TreeStructure) // not printed
    println("inspectSimple1 end")
    x
  }

  inline def inspect(inline x: Any): Any = ${ inspectSimple1('x) }

  inline def inspectClassTyped[T](inline x: T) = ${ getASTinfoTuple('x) } //

  def exprMonadTest(x: Expr[Int])(using Quotes) = {
    import scala3features.exprMonad._
    x.flatMap(y => Expr(y))
    for {
      i <- x
      j <- x
    } yield i + j
  }

// https://docs.scala-lang.org/scala3/guides/macros/reflection.html#to-symbol-and-back
  def getASTinfoTuple[T: Type](x: Expr[T])(using Quotes) = {

    import quotes.reflect.*

    println("getASTinfoTuple")
    val tpe: TypeRepr = TypeRepr.of[T]
    val sybs: Symbol = tpe.typeSymbol
    println(sybs.fullName)
    val pos = sybs.pos.get
    val codeLine = pos.startLine.toString
    // rust's dbg . https://blog.softwaremill.com/starting-with-scala-3-macros-a-short-tutorial-88e9d2b2584c
    // println(s"code position: ${pos.sourceFile.name} ln:" + codeLine)
    val decl: List[Symbol] = sybs.declarations
    val decl1 = sybs.declaredMethods
    // decl.foreach(s => println(s.show(using Printer.TreeStructure)))
    pprintln(decl)
    val retStr = sybs.toString() + "," + decl
    val sybsE = Expr(retStr)
    // println("sybsE:" + sybsE)
    val resu: Expr[(T, String)] = '{ ($x, $sybsE) }
    // println("quoted:" + resu.show)
    resu
  }

  def code(x: Expr[Int])(using Quotes) = {
    println(x.show)
    List(1).map(x => x)

    '{ $x + 1 }
  }

// https://github.com/sirthias/macrolizer
  /*
     macrolizer.show {
    val x = 1
  }
   */
  // '{ val y = 3; ${ code('y) } } // y+1

//check if n is a quoted constant
  def powCodeFor(x: Expr[Int], n: Expr[Int])(using Quotes) = {
    n match
      case Expr(m) => // powCode(x, m) // it is a constant: unlift code n='{m} into number m
      case _       => // '{ power($x, $n) } // not known: call power at runtime
  }

}
