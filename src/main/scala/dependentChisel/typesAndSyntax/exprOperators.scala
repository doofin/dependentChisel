package dependentChisel.typesAndSyntax

import typesAndOps.{Expr, BinOp, ExprAsBool}

trait exprOperators {
//int ops
  extension [w <: Int](x: Expr[w]) {
    def +(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "+")
    def -(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "-")
    def *(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "*")
    def /(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "div")
    // def ===(oth: Expr[w]) = Bool(x, oth)
    /* TODO allow dym mixed with typed without casting? */
    def ===(oth: Expr[w]): ExprAsBool[w] = ExprAsBool(BinOp(x, oth, "=="))
  }

// bool ops
  extension [w <: Int](x: ExprAsBool[w]) {
    def |(oth: ExprAsBool[w]) = ExprAsBool(BinOp(x, oth, "or"))
    def &(oth: ExprAsBool[w]) = ExprAsBool(BinOp(x, oth, "and"))
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
