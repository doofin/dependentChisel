package dependentChisel.staticAnalysis

/** lift domain to domainMap where both are lattices. domain ->> var->domain ->> prog point
  * ->var->domain
  */
object MonotoneFramework {
  type VarName = String
  // type level lambda from domain to Map[VarName, domain]
  type VarMap[domain] = Map[VarName, domain]

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
  case class MonoFrameworkT[T, stmtT](
      val transferFn: ((Int, stmtT, Int), T) => T,
      // val botMap: VarMap[T],
      baseLattice: semiLattice[T]
  ) extends semiLattice[T] {
    // val liftedLattice = baseLattice.liftWithMap(botMap)
    // override val bottom: VarMap[T] = liftedLattice.bottom
    // override val lub = liftedLattice.lub
    // override val leq = liftedLattice.leq

    override val bottom: T = baseLattice.bottom
    override val lub = baseLattice.lub
    override val leq = baseLattice.leq

    /** run the monotone framework on a program graph
      *
      * @param progGraph
      * @param isForward
      *   true for forward analysis,false for backward analysis
      * @return
      */
    def runWithProgGraph(
        progGraph: List[(Int, stmtT, Int)],
        isForward: Boolean = true,
        entryExitPoint: (Int, Int)
    ) = {
      val mf = this

      worklistAlgo.onProgGraph(
        progGraph,
        mf.transferFn,
        lattice = baseLattice,
        initD = mf.bottom,
        entryExitPoint = entryExitPoint,
        isForward = isForward
      )
    }
  }

  /** lift semiLattice[t] to semiLattice[VarMap[t]]
    *
    * useful for some static analysis like interval analysis, sign analysis
    */
  extension [t](base: semiLattice[t]) {

    /** lift semiLattice[t] to semiLattice[VarMap[t]]
      *
      * where VarMap[t] represents VarName->t
      *
      * @param botMap
      *   all variable names as bottom element
      * @return
      */
    def liftToVarMap(botMap: VarMap[t]): semiLattice[VarMap[t]] =
      new semiLattice[VarMap[t]] {
        override val leq: (VarMap[t], VarMap[t]) => Boolean = { (m1, m2) =>
          m1 forall { k1 =>
            // im1 is subset of im2
            val i1o = k1._2
            val i2o = m2(k1._1)

            base.leq(i1o, i2o)
          }
        }
        /*  for lub, take union of keys, for each key do lub on values
         */
        override val lub: (VarMap[t], VarMap[t]) => VarMap[t] = { (m1, m2) =>
          val newmap =
            (m1.keys ++ m2.keys).toSet map { k =>
              val i1o = m1(k)
              val i2o = m2(k)
              val rr = base.lub(i1o, i2o)
              (k, rr)
            }
          Map(newmap.toSeq*)
        }

        override val bottom: VarMap[t] =
          botMap.map(s => (s._1, base.bottom))
      }
  }
}
