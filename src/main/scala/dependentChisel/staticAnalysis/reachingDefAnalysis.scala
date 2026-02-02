package dependentChisel.staticAnalysis

import dependentChisel.staticAnalysis.MonotoneFramework.*
import dependentChisel.codegen.sequentialCommands.*
import dependentChisel.typesAndSyntax.typesAndOps.*

/** reaching definitions as a mapping Var -> PowerSet( Q? * Q ) */
object reachingDefAnalysis {
  // (x, p, q) means variable x is modified by the edge p->q. (x,?,qi) means x is not modified before initial point qi. Here we don't use ? for simplicity
  type Domain = Set[(VarName, Int, Int)] // PowerSet( Q? * Q )
  type Stmt = AtomicCmds // statements

  def transferFn(ppoint: (Int, Stmt, Int), l: Domain): Domain = {
    val stmt = ppoint._2
    val gs = genSet(ppoint)
    val res = stmt match {
      case WeakStmt(lhs, ":=", rhs, prefix) =>
        // res = l- kill + gen
        val killed = l.filter(_._1 != lhs.getName)
        killed ++ gs
      case _ => l
    }

    println(s"tran for $ppoint : in = $l ,  gen = $gs , out = $res")
    res
  }

  def genSet(ppoint: (Int, Stmt, Int)): Domain = {
    val (p, stmt, q) = ppoint
    stmt match {
      case WeakStmt(lhs, op, rhs, prefix) =>
        Set((lhs.getName, p, q))
      case _ => Set.empty
    }
  }

  object RDLattice extends semiLattice[Domain] {

    override val leq = (a: Domain, b: Domain) => a.subsetOf(b)

    override val lub = (a: Domain, b: Domain) => a.union(b)

    override val bottom: Domain = Set.empty
  }

  def monoFramework(
  ) = {

    MonoFrameworkT(
      transferFn = transferFn,
      lattice = RDLattice
    )
  }
}
