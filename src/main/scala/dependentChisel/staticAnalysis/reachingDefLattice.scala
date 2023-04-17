package dependentChisel.staticAnalysis

/** reaching definitions as a mapping Var -> PowerSet( Q? * Q ) */
object reachingDefLattice {
  type domain = Set[(Int, Int)] // PowerSet( Q? * Q )
// domainMap is  Var -> PowerSet( Q? * Q ) uninitializedLattice

  /* For this define Bot to be the mapping that maps each variable to the empty set */

}
