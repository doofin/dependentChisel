package dependentChisel.staticAnalysis

/** a complete lattice is a partially ordered set in which all subsets have both a supremum (join)
  * and an infimum (meet).
  *
  * A complete lattice always has a least and greatest element
  *
  * A pointed semi-lattice (or upper semilattice) (L, <=) is a partially ordered set such that all
  * finite subsets Y of L have a least upper bound.
  *
  * The rational number Q with the usual linear order, is an distributive lattice which is not
  * complete, while the real number R is complete.
  *
  * @tparam t
  */
trait semiLattice[t] {

  /** partial ordering, less than or equal to
    */
  val leq: (t, t) => Boolean

  /** least upper bound
    */
  val lub: (t, t) => t // least upper bound

  /** least element, bottom
    */
  val bottom: t
}
