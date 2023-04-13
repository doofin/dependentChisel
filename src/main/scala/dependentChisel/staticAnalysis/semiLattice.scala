package dependentChisel.staticAnalysis

/** a complete lattice is a partially ordered set in which all subsets have both
  * a supremum (join) and an infimum (meet).
  *
  * A complete lattice always hasa least and greatest element
  *
  * A pointed semi-lattice (or upper semilattice) (L, <=) is a partially ordered
  * set such that all finite subsets Y of L have a least upper bound.
  *
  * The set Q of all rational numbers, with the usual linear order, is an
  * infinite distributive lattice which is not complete.
  * @tparam domain
  *   satisify acc
  */
trait semiLattice[domain] {
  val smallerThan: (domain, domain) => Boolean // partial ordering
  val lub: (domain, domain) => domain // least upper bound
  val bottom: domain
}
