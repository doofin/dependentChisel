package dependentChisel.typesAndSyntax

import typesAndOps.*
import dependentChisel.typesAndSyntax.typesAndOps.UniOp

import scala.compiletime.ops.int.*
import scala.compiletime.*

trait exprOperators {
  // int ops
  extension [w <: Int](x: Expr[w]) {
    def +(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "+")
    def ++(oth: Expr[w]) = AddOp(x, oth, "+")

    def -(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "-")
    def *(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "*")
    def /(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "div")

    /* relational ops */
    def ===(oth: Expr[w]): Expr[1] = BinOp(x, oth, "==").asTypedUnsafe[1]
    def >(oth: Expr[w]) = BinOp(x, oth, "gt").asTypedUnsafe[1]
    def <(oth: Expr[w]) = BinOp(x, oth, "lt").asTypedUnsafe[1]
    /* TODO allow dym mixed with typed without casting? */

  }

// bool ops
  extension (x: Expr[1]) {
    def |(oth: Expr[1]) = BinOp(x, oth, "or")
    def &(oth: Expr[1]) = BinOp(x, oth, "and")
    def unary_~ = UniOp[1](x, "not")
  }

  /* sealed trait Expr[w <: Int] {
    def +(oth: Expr[w]) = BinOp(this, oth, "+")
    def *(oth: Expr[w]) = BinOp(this, oth, "*")
    def -(oth: Expr[w]) = BinOp(this, oth, "-")
    def |(oth: Expr[w]) = BinOp(this, oth, "|")
    def &(oth: Expr[w]) = BinOp(this, oth, "&")
    def ===(oth: Expr[w]) = Bool(this, oth)
  } */

}
