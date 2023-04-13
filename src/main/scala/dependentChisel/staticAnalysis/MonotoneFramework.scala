package dependentChisel.staticAnalysis

/** lift domain to domainMap where both are lattices */
object MonotoneFramework {
  type VarName = String
  type domainMapT = [domain] =>> Map[VarName, domain]

  /** @tparam domain
    *   satisify acc
    * @tparam stmtT
    *   type of statement
    */
  trait MonotoneFrameworkProgGr[domain, stmtT]
      extends semiLattice[domainMapT[domain]] {
    // type domainMap = Map[String, domain] // var name to domain

    val transferF: ((Int, stmtT, Int), domainMapT[domain]) => domainMapT[domain]
    val init: domain

    // val smallerThan: (domainMap, domainMap) => Boolean
    // val lub: (domainMap, domainMap) => domainMap
    // val bottom: domain
  }
}
