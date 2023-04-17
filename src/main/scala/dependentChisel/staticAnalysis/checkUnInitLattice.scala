package dependentChisel.staticAnalysis

/** reaching definitions as a mapping Program point-> Var -> bool. only need to define
  * last part bool saying whether var have an value
  */
object checkUnInitLattice {
  type domain = Boolean // PowerSet( Q? * Q )
  object lattice extends semiLattice[domain] {

    override val smallerThan: (domain, domain) => Boolean = {
      case (_, true)      => true
      case (false, false) => true
      case _              => false
    }

    override val lub: (domain, domain) => domain = {
      _ || _
    }

    override val bottom: domain = false

  }

}
