package dependentChisel.staticAnalysis

import dependentChisel.codegen.seqCommands.FirStmt
import dependentChisel.staticAnalysis.MonotoneFramework.domainMapT
import dependentChisel.staticAnalysis.uninitializedLattice
import dependentChisel.staticAnalysis.MonotoneFramework.MonotoneFrameworkProgGr

/** uninitializedAnalysis
  */
object uninitializedAnalysis {
  type mDomain = uninitializedLattice.domain
  type mStmt = FirStmt

  case class uninitializedAsMonotoneFramework(
      initMap: domainMapT[mDomain]
  ) extends MonotoneFrameworkProgGr[mDomain, mStmt](initMap) {

    override val baseLattice: semiLattice[mDomain] = ???

    override val transferF
        : ((Int, mStmt, Int), domainMapT[mDomain]) => domainMapT[mDomain] = {
      case ((q0, prog, q1), varmap) => ???
    }

    override val init: mDomain = ???
  }
}
