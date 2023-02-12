// // import
// import dependentChisel.*
// import scala.compiletime.ops.int.*
// import scala.compiletime.*

// import com.doofin.stdScalaJvm.*

// import dependentChisel.syntax.dslAST.wireTp

// /*
//  old structual type
//  works only when run in sbt */
// val io = new {
//   val sw = 1
//   val led = 2

// }

// // use tuples as dependent type
// val t1: (1, 2) = (1, 2)
// case class tupDep[a <: Int, b <: Int](x: (a, b), y: (a, b)) {}
// case class tupDep2[a <: (Int, Int)](x: a, y: a) {}
// tupDep[1, 2](t1, (1, 2))
// // tupDep[1, 2](t1, (1, 3)) // ok,will err
// tupDep2[(1, 2)](t1, (1, 2))
// // tupDep2[(1, 2)](t1, (1, 3))  // ok,will err

// /* try to put type info in a composite "bundle" type
// firrtl io example(from chisel bundle): output io : { flip a : UInt<32>, flip b : UInt<32>, y : UInt<32>}
// here we actually wish to utilize scala's class def for named tuples as DSL
// aim to gather type info in bundle
//  */

// //keep precise type with tuples
// import dependentChisel.syntax.bundles.*
// val outIo2 = ("a" -> UIntDep[32](), "b" -> UIntDep[2]())
// // with case class,fixed,types only in member
// case class outIo2c(a: UIntDep[32], b: UIntDep[2])
// // with case class,fixed ,have type info in sig
// case class outIo2c2[t1, t2](a: t1, b: t2)
// // outIo2c(???, ???)
// outIo2c2(UIntDep[1](), UIntDep[2]())

// //lose precise type with lists
// val outIoList = List("a" -> UIntDep[32](), "b" -> UIntDep[2]())

// /* more tedious way to define a module ,as in chisel */
// import dependentChisel.syntax.bundlesAsTuple.*
// val adder1 = adder()
// val bd1 = adder1.createBundle
// adder1.toFir
// import dependentChisel.macros
// macros.inspect(bd1)
// val u2 = bd1.t._2._2
// u2.valu

// import dependentChisel.syntax.bundles.*

// import chiselAdder.*
// val bd2 = MyBundle1()
// // bd2.
// // constValueOpt[bd2.p1.type]
// // constValueOpt[bd2.v1.type]
// // constValue[u2.type]

// case class bundle3(p1: UIntDep[1] = UIntDep(), p2: UIntDep[2] = UIntDep()) extends MyBundle[bundle3] {
//   inline def v1: UIntDep[1] = UIntDep()
//   val v2: UIntDep[1] = UIntDep()
// }

// val bd3 = bundle3()
// // type t1 = bd3.v1.type
// // constValueOpt[t1]
