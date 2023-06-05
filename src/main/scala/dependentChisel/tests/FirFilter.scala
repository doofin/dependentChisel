package dependentChisel.tests

object FirFilter {
  class FirFilter[width <: Int: ValueOf](coeffs: Seq[Int]) {}
}
