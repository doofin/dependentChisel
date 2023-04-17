package dependentChisel.staticAnalysis

/** reaching definitions as a mapping Program point-> Var -> bool. only need to
  * define last part bool saying whether var have an value
  */
object uninitializedLattice {
  type domain = Boolean // PowerSet( Q? * Q )
// domainMap is  Var ->

  /* For this define Bot to be the mapping that maps each variable to the empty set */

}
