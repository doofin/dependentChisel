package dependentChisel.staticAnalysis

import monocle.*

import dependentChisel.staticAnalysis.MonotoneFramework.*
import dependentChisel.codegen.sequentialCommands.*
import dependentChisel.typesAndSyntax.typesAndOps.*

/** live variable analysis is a backward analysis
  *
  * we want to know which variables are live (will be used afterwards) at each program point.
  *
  * For example, var x is live at program point p, if there is a path downstream from p that uses x
  * and x is not re-defined(we consider re-definition as kill)
  */
object liveVarAnalysis {
  type Domain = Set[VarName]
  type Stmt = AtomicCmds // statements

  /** free var in a expr, which is just all var names
    */
  def fv(e: Expr[_]): Set[VarName] = {
    e match
      case VarLit(name)    => Set(name)
      case BinOp(a, b, nm) => fv(a) ++ fv(b)
      case UniOp(a, nm)    => fv(a)
      case _               => Set.empty[VarName]
  }

  def genSetLV(s: Stmt): Set[VarName] = s match
    case WeakStmt(lhs, op, rhs, prefix) => fv(rhs)
    case _                              => Set.empty[VarName]

  def killSetLV(s: Stmt): Set[VarName] = s match {
    case WeakStmt(lhs, op, rhs, prefix) => Set(lhs.getName)
    case _                              => Set.empty[VarName]
  }

  /** transfer function for living variable analysis
    *
    * Stmt, Lattice => Lattice
    */
  def transferLV(ppoint: (Int, Stmt, Int), l: Domain): Domain = {
    val stmt = ppoint._2
    val ks = killSetLV(stmt).toSet
    val gs = genSetLV(stmt).toSet
    val res = (l -- ks) union gs
    println(s"tran for $ppoint : in = $l , kill = $ks , gen = $gs , out = $res")
    res
  }

  /** lattice for live variable analysis is Set[Var],if a var is in the set, it is live
    */
  object liveVarLattice extends semiLattice[Domain] {

    override val leq = (a: Set[VarName], b: Set[VarName]) => a.subsetOf(b)

    override val lub = (a: Set[VarName], b: Set[VarName]) => a.union(b)

    override val bottom = Set.empty[VarName]

  }

  def monoFramework(
      mBotMap: Domain
  ) = {

    MonoFrameworkT(
      transferFn = transferLV,
      baseLattice = liveVarLattice
    )
  }
}
