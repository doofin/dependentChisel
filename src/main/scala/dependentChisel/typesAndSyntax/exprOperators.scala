package dependentChisel.typesAndSyntax

import typesAndOps.{Expr, BinOp, BoolExpr}

trait exprOperators {
//int ops
  extension [w <: Int](x: Expr[w]) {
    def +(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "+")
    def -(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "-")
    def *(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "*")
    def /(oth: Expr[w]): BinOp[w] = BinOp(x, oth, "div")
    // def ===(oth: Expr[w]) = Bool(x, oth)
    def ===(oth: Expr[w]): BoolExpr[w] = BoolExpr(BinOp(x, oth, "=="))
  }

// bool ops
  extension [w <: Int](x: BoolExpr[w]) {
    def |(oth: BoolExpr[w]) = BoolExpr(BinOp(x, oth, "or"))
    def &(oth: BoolExpr[w]) = BoolExpr(BinOp(x, oth, "and"))
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
