import com.doofin.stdScalaJvm.*
import scala.compiletime.*
import scala.compiletime.ops.int.*

// import dependentChisel.syntax.dslAST.wireTp
import dependentChisel.depTypes.*

import dependentChisel.typesAndSyntax.typesAndOps.*
/* simple tests for compile/ runtime dependent values */
// test constValueOpt
constValueOpt[2]

val n2 = List(1, 2, 3).sum
// inline val n2 = List(1, 2, 3).sum
val n3 = 3
inline val n4 = 3
// inline val n5 = n2 //err
constValueOpt[n2.type]
constValueOpt[n3.type]
constValueOpt[n4.type]

// test wire type
// wireTp[1]().getVal //can get value
// wireTp().getVal //can't get value
// wireTp[n4.type]().getVal

wireTp2[(1, n4.type)]().getVal
wireTp2[(1, 2)]().getVal

inline val size = 1

constValueOpt[size.type]

// em1 =:= em1
val r = Lit[1](1) match {
  case x: Lit[w] => constValueOpt[w].toString()
  case x         => x.toString()
}

inline def int2hexAndCeiling(i: Int) = {
  val ceiling = (math.log(i) / math.log(2)).ceil.toInt
  (i.toHexString, ceiling)
}

val i3 = int2hexAndCeiling(2)

class c1[i <: Int] { inline def sz = constValueOpt[i] }
(new c1[1]).sz
// Lit[i3._2.type](1)

class c2[i <: Int] { println("constValueOpt:" + constValueOpt[i]) }
new c2[2]

// r2.rr
