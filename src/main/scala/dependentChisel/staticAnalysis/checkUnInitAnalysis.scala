package dependentChisel.staticAnalysis

import dependentChisel.codegen.seqCommands.AtomCmds
import dependentChisel.staticAnalysis.MonotoneFramework.domainMapT
import dependentChisel.staticAnalysis.checkUninitLattice

import dependentChisel.staticAnalysis.MonotoneFramework.MonoFrameworkT
import dependentChisel.codegen.seqCommands.NewInstStmt
import dependentChisel.codegen.seqCommands.FirStmt
import dependentChisel.codegen.seqCommands.VarDecls

/** uninitializedAnalysis
  */
object checkUnInitAnalysis {
  type mDomain = checkUninitLattice.domain
  type mStmt = AtomCmds // assign,var decls etc

  case class MonoFramework(
      mInitMap: domainMapT[mDomain]
  ) extends MonoFrameworkT[mDomain, mStmt](
        mInitMap,
        checkUninitLattice.lattice
      ) {

    // override val baseLattice: semiLattice[mDomain] = uninitializedLattice.lattice //bug! will cause null
    override val transferF
        : ((Int, mStmt, Int), domainMapT[mDomain]) => domainMapT[mDomain] = {
      case ((q0, cmd, q1), varmap) =>
        cmd match {
          case FirStmt(lhs, op, rhs, prefix) =>
            if op == ":=" then varmap.updated(lhs.getname, true) else varmap
          // case NewInstStmt(instNm, modNm)    =>
          // case VarDecls(v)                   =>
          case _ => varmap
        }
    }

    override val init: mDomain = false
  }
}
