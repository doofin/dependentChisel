package dependentChisel.staticAnalysis

/** reaching Def: aim to use this for detect uninitialized vars.for each
  * variable x we can obtain a set of pairs of nodes {(q1 , q2 )} telling where
  * the variable x might have been defined. Analysis domains. To represent this
  * more directly we could decide to represent reaching definitions as a mapping
  * Var -> PowerSet( Q? * Q )
  */
object reachingDefAnalysis {}
