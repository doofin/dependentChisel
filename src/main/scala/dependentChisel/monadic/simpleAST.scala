package dependentChisel.monadic

import dependentChisel.monadic.monadicAST.BoolExpr

object simpleAST {
  enum Stmt {
    case Top()
    case If(cond: BoolExpr)
    case Start(ctrl: Stmt)
    case End(ctrl: Stmt)
    case Assign(assign: String)
    case Decl(decl: String)
    case Stmts(stmt: List[Stmt])
  }
}
