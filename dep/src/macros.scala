/*  Quoting and splicing are combined with inline to cause this macro implementation
to do compile-time metaprogramming.
 */
import scala.quoted.* // imports Quotes, Expr

object macros {
  type PlusTwo[T <: Int] = scala.compiletime.ops.int.+[2, T]

  inline def repeat(s: String, count: Int): String =
    if count == 0 then ""
    else s + repeat(s, count - 1)

  inline def doSomething(inline mode: Boolean): Unit =
    if mode then "" else ""

  val a: PlusTwo[4] = 6
  doSomething(true)
  repeat("hello", 3) // Okay

  var n = 3
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
  def inspectCode(x: Expr[Any])(using Quotes): Expr[Any] =
    println(x.show)
    x

  inline def inspect(inline x: Any): Any = ${ inspectCode('x) }
}
