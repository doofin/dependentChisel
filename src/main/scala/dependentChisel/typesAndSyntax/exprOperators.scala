package dependentChisel.typesAndSyntax

import typesAndOps.{Expr, BinOp}

trait exprOperators {
//int ops
  extension [w <: Int](x: Expr[w]) {
    def +(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "+")
    def -(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "-")
    def *(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "*")
    def /(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "div")
    // def ===(oth: Expr[w]) = Bool(x, oth)
    /* TODO allow dym mixed with typed without casting? */
    def ===(oth: Expr[w]): Expr[1] = BinOp(x, oth, "==").asTypedUnsafe[1]
  }

// bool ops
  extension (x: Expr[1]) {
    def |(oth: Expr[1]) = BinOp(x, oth, "or")
    def &(oth: Expr[1]) = BinOp(x, oth, "and")
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
