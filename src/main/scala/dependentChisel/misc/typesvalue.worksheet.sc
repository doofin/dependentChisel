import com.doofin.stdScalaJvm.*
import scala.compiletime.*
import scala.compiletime.ops.int.*

// import dependentChisel.syntax.dslAST.wireTp
import dependentChisel.misc.depTypes.*

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

def add1[I <: Int](i: I): I + 1 = (i + 1).asInstanceOf[I + 1]

val v1: 2 = add1[1](1)

// val v2: 3 = add1[1](1) // should err

def add11[I <: Int](i: I): I + 1 = (i + 2).asInstanceOf[I + 1]

add11(1)

// val a1: 2 = add1(1) // not work?

// r2.rr

case class FinN[I <: Int]() { inline def pt = println(constValueOpt[I]) }

val i1 = 2
FinN[1]().pt
FinN[i1.type]().pt

val i2 = (1 to 3).sum
val i4 = (1 to 3).sum
class c3[i <: Int: ValueOf] { println("valueOf:" + valueOf[i]) }

def addc3[i <: Int: ValueOf](a: c3[i], b: c3[i]) = {}
// addc3(c3[i2.type], c3[i4.type])

new c3[2]

new c3[i2.type]

// addc3(new c3[i2.type], new c3[i4.type]) // won't compile
addc3(new c3[1], new c3[1]) // ok

def add1i(i: Int): i.type + 1 = (i + 10).asInstanceOf[i.type + 1]

// def add1[I <: Int](i: I): I + 1 = (i + 10).asInstanceOf[I + 1]
val a: 1 = add1i(0)
