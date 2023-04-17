package dependentChisel.staticAnalysis

/** lift domain to domainMap where both are lattices. domain ->> var->domain ->> prog
  * point ->var->domain
  */
object MonotoneFramework {
  type VarName = String
  type domainMapT = [domain] =>> Map[VarName, domain]

  /** usage : give initMap: domainMapT[domain] and baseLattice, then override
    * transferF(transfer function) .
    *
    * Recommend : create two file named xxAnalysis impl this trait, and xxLattice impl
    * just lattice
    * @tparam domain
    *   domain lattice which satisify acc
    * @tparam stmtT
    *   type of statement
    */
  trait MonoFrameworkT[domain, stmtT](
      val initMap: domainMapT[domain],
      baseLattice: semiLattice[domain]
  ) extends semiLattice[domainMapT[domain]] {
    // type domainMap = Map[String, domain] // var name to domain

    /** src point,stmt,tgt point,prevMap=>newMap */
    // val baseLattice: semiLattice[domain]
    val transferF: ((Int, stmtT, Int), domainMapT[domain]) => domainMapT[domain]
    val init: domain

    /** lift from semiLattice[domain] to semiLattice[domainMapT[domain]] */
    val liftedLattice = baseLattice.liftWithMap(initMap)
    override val bottom: domainMapT[domain] = liftedLattice.bottom
    override val lub = liftedLattice.lub
    override val smallerThan = liftedLattice.smallerThan

    def runWithProgGraph(
        progGraph: List[(Int, stmtT, Int)]
    ): Map[Int, domainMapT[domain]] =
      worklistAlgo.wlAlgoMonotone(this, progGraph)
  }

  extension [domain](lattice: semiLattice[domain]) {
    def liftWithMap(initMap: domainMapT[domain]) =
      new semiLattice[domainMapT[domain]] {
        override val smallerThan: (domainMapT[domain], domainMapT[domain]) => Boolean = {
          (m1, m2) =>
            m1 forall { k1 =>
              // im1 is subset of im2
              val i1o = k1._2
              val i2o = m2(k1._1)

              lattice.smallerThan(i1o, i2o)
            }
        }
        override val lub
            : (domainMapT[domain], domainMapT[domain]) => domainMapT[domain] = {
          (m1, m2) =>
            val newmap =
              (m1.keys ++ m2.keys).toSet map { k =>
                val i1o = m1(k)
                val i2o = m2(k)
                val rr = lattice.lub(i1o, i2o)
                (k, rr)
              }
            Map(newmap.toSeq*)
        }

        override val bottom: domainMapT[domain] =
          initMap.map(s => (s._1, lattice.bottom))
      }
  }
}
