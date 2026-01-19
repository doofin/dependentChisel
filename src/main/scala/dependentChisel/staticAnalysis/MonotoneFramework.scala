package dependentChisel.staticAnalysis

/** lift domain to domainMap where both are lattices. domain ->> var->domain ->> prog point
  * ->var->domain
  */
object MonotoneFramework {
  type VarName = String
  // type level lambda from domain to Map[VarName, domain]
  type domainMapT[domain] = Map[VarName, domain]

  /** enrich the lattice with transfer function and initial mapping
    *
    * tips : create two file named xxAnalysis to implement this trait, and xxLattice to implement
    * the lattice separately.
    * @tparam domain
    *   domain lattice which satisify acc
    * @tparam stmtT
    *   type of statement
    *
    * @param initMap
    *   initial mapping from var to domain value
    * @param baseLattice
    *   lattice for domain
    */
  trait MonoFrameworkT[domain, stmtT](
      val transferF: ((Int, stmtT, Int), domainMapT[domain]) => domainMapT[domain],
      val botMap: domainMapT[domain],
      baseLattice: semiLattice[domain]
  ) extends semiLattice[domainMapT[domain]] {
    // type domainMap = Map[String, domain] // var name to domain

    // lift domain to domainMap[domain] lattice
    val liftedLattice = baseLattice.liftWithMap(botMap)
    override val bottom: domainMapT[domain] = liftedLattice.bottom
    override val lub = liftedLattice.lub
    override val smallerThan = liftedLattice.smallerThan

    def runWithProgGraph(
        progGraph: List[(Int, stmtT, Int)]
    ): Map[Int, domainMapT[domain]] =
      worklistAlgo.wlAlgoMonotone(this, progGraph)
  }

  /** lift any t to string->t semiLattice
    *
    * that is, for t:SemiLattice, the function space String->t is also a SemiLattice
    */
  extension [domain](base: semiLattice[domain]) {

    /** lift the t:lattice to string->t lattice (function space)
      *
      * @param botMap
      *   the bottom element mapping, since we need to know var names
      * @return
      */
    def liftWithMap(botMap: domainMapT[domain]): semiLattice[domainMapT[domain]] =
      new semiLattice[domainMapT[domain]] {
        override val smallerThan: (domainMapT[domain], domainMapT[domain]) => Boolean = {
          (m1, m2) =>
            m1 forall { k1 =>
              // im1 is subset of im2
              val i1o = k1._2
              val i2o = m2(k1._1)

              base.smallerThan(i1o, i2o)
            }
        }
        /*  for lub, take union of keys, for each key do lub on values
         */
        override val lub: (domainMapT[domain], domainMapT[domain]) => domainMapT[domain] = {
          (m1, m2) =>
            val newmap =
              (m1.keys ++ m2.keys).toSet map { k =>
                val i1o = m1(k)
                val i2o = m2(k)
                val rr = base.lub(i1o, i2o)
                (k, rr)
              }
            Map(newmap.toSeq*)
        }

        override val bottom: domainMapT[domain] =
          botMap.map(s => (s._1, base.bottom))
      }
  }
}
