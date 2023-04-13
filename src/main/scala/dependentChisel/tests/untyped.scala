package dependentChisel.tests

import dependentChisel.typesAndSyntax.basicTypes.*

object untyped {
  val a1 = IO(1)
  val a2 = IO(2)
  val a3 = a1 + a2
}
