package dependentChisel.staticAnalysis

import dependentChisel.codegen.sequentialCommands.AtomicCmds
import dependentChisel.staticAnalysis.MonotoneFramework.domainMapT
import dependentChisel.staticAnalysis.checkUnInitLattice

import dependentChisel.staticAnalysis.MonotoneFramework.MonoFrameworkT
import dependentChisel.codegen.sequentialCommands.NewInstance
import dependentChisel.codegen.sequentialCommands.WeakStmt
import dependentChisel.codegen.sequentialCommands.VarDecls

/** check if vars have an value
  */
object checkUnInitAnalysis {
  type mDomain = checkUnInitLattice.domain
  type mStmt = AtomicCmds // assign,var decls etc

  case class MonoFramework(
      mInitMap: domainMapT[mDomain]
  ) extends MonoFrameworkT[mDomain, mStmt](
        mInitMap,
        checkUnInitLattice.lattice
      ) {

    // override val baseLattice: semiLattice[mDomain] = uninitializedLattice.lattice //bug! will cause null
    override val transferF
        : ((Int, mStmt, Int), domainMapT[mDomain]) => domainMapT[mDomain] = {
      case ((q0, cmd, q1), varmap) =>
        cmd match {
          case WeakStmt(lhs, op, rhs, prefix) =>
            if op == ":=" then varmap.updated(lhs.getname, true) else varmap
          // case NewInstStmt(instNm, modNm)    =>
          // case VarDecls(v)                   =>
          case _ => varmap
        }
    }

    override val init: mDomain = false
  }
}
