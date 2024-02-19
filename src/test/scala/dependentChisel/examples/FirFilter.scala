package dependentChisel.examples

object FirFilter {
  class FirFilter[width <: Int: ValueOf](coeffs: Seq[Int]) {}
}
