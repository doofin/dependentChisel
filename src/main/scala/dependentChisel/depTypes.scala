package dependentChisel

/*partial DependentTypes via compile time inline and checks */
import scala.compiletime.ops.int.*
import scala.compiletime.ops.int.S
import scala.compiletime.*

object depTypes {
  def run = {
    val dlist1 = 1 :: 2.2 :: DTNil
    val fixedList1 = 1 :: 2 :: 3 :: FixLenNil()

    // 1.add(2): 3
    println(dlist1)
    // wire(1) // val res0: Int + 2 = 3
    val dum1 = 1: Rank[1]
    val w0 = wire0: Rank[0]
    val v2 = 2: S[1]
    val s2 = (1 + 1): 2
    val num7: 1 + 2 * 3 = 7
    // val num72: 1 = 7
    val sum: 2 + 2 = 4
    wireId(2): 2
    // wireId2(2): 2 //fail
    wireAdd2[4]: 6 // ok
    wireConn(wireNew[1], wireNew[1]) // ok
    // wireConn(wireNew[1], wireNew[2]) // fail
  }

  sealed trait DList[len <: Int]: // N is a singleton subtype of Int
    inline def size: len = valueOf[len] // <2>

    def ::[T <: Matchable](h: T): DTCons[len, T, this.type] = // <3>
      DTCons(h, this)

  case object DTNil extends DList[0] // <4>

  case class DTCons[n <: Int, T <: Matchable, dList <: DList[n]]( // <5>
      head: T,
      tail: dList
  ) extends DList[S[n]]

  /* fixed length lists */
  sealed trait FixLenList[
      len <: Int,
      T <: Matchable
  ]: // N is a singleton subtype of Int
    inline def size: len = valueOf[len] // <2>

    def ::(h: T): FixLenCons[len, T, this.type] = // <3>
      FixLenCons(h, this)

  case class FixLenNil[T <: Matchable]() extends FixLenList[0, T] // <4>

  case class FixLenCons[n <: Int, T <: Matchable, dList <: FixLenList[n, T]]( // <5>
      head: T,
      tail: dList
  ) extends FixLenList[S[n], T]
// defines arithmetic on type
// https://github.com/MaximeKjaer/tf-dotty/blob/master/modules/compiletime/src/main/scala/io/kjaer/compiletime/dependent.scala
  extension [X <: Int, Y <: Int](x: Int) {
    infix def add(y: Y): X + Y = (x + y).asInstanceOf[X + Y]
    infix def sub(y: Y): X - Y = (x - y).asInstanceOf[X - Y]
    infix def mul(y: Y): X * Y = (x * y).asInstanceOf[X * Y]
    infix def lt(y: Y): X < Y = (x < y).asInstanceOf[X < Y]
    infix def le(y: Y): X <= Y = (x <= y).asInstanceOf[X <= Y]
    // def add1: S[X] = x + 1
    // infix inline def add2(y: Y): X + Y = x.asInstanceOf[X] + y.asInstanceOf[Y]
    // infix def add2(y: Y): X + Y = x.asInstanceOf[X] + y.asInstanceOf[Y]
  }
  type Rank[X <: Int] <: Int = X match {
    case 0 => 0
    case 1 => 1
    // case x => x
  }
  /* define compile time check wires
  https://github.com/MaximeKjaer/tf-dotty/blob/master/modules/compiletime/src/main/scala/io/kjaer/compiletime/Shape.scala
  must use inline for  valueOf!
  without inline ,will be err :cannot reduce summonFrom
   */
  case class wireTp[I <: Int, X](x: X)
  case class wireTp2[xy <: (Int, Int)]() {
    inline def getVal = constValueOpt[xy]
  }
  // case class wireTp2[I <: Int, X](x: X)

  inline def wireAdd2[X <: Int]: X + 2 = {
    val singV = valueOf[X]
    singV.add(2)
  }

  inline def wireConn[n <: Int](x: wireTp[n, Int], y: wireTp[n, Int]) = {}
  inline def wireConcat[n <: Int, m <: Int](
      x: wireTp[n, Int],
      y: wireTp[m, Int]
  ): wireTp[n + m, Int] = {
    wireTp[n + m, Int](1)
  }

  inline def wireNew[n <: Int]
      : wireTp[n + 2, Int] = { // must use inline for  valueOf!
    val singV = valueOf[n]
    wireTp(1) // singV.add(2)
  }

// misc check
  def wireId[X <: Int](x: X): X = { x }
  def wireId2(x: Int) = { x } // won't work for type anno
  def wire2[X <: Int](x: X) = { x add 2 }
  // def wireSuc[X <: Int](x: X): S[X] = { x + 1 } //fail
  def wire0: Rank[0] = 0
// def wire2[X <: Int] = { valueOf(X).add(2) }

  type Elem[X] = X match
    case String      => Char
    case Array[t]    => t
    case Iterable[t] => t

  enum Adt1 {
    case High, Low
  }

  def add[t <: Adt1](x: t, y: t) = {}

  add(Adt1.High, Adt1.Low)

  case class Wrp[t <: Adt1](x: t)
  def add[t <: Adt1](x: Wrp[t], y: Wrp[t]) = {}

  add(Wrp(Adt1.High), Wrp(Adt1.Low)) // not work

  enum Adt2[n <: Int] {
    case High() extends Adt2[1]
    case Low() extends Adt2[2]
  }
  def add2[n <: Int, t <: Adt2[n]](x: t, y: t) = {}
  add2(Adt2.High(), Adt2.High())
  // add2(Adt2.High(), Adt2.Low()) // fail,ok

  enum Adt3[n <: Adt1] {
    case High() extends Adt3[Adt1.High.type]
    case Low() extends Adt3[Adt1.Low.type]
  }

  def add3[n <: Adt1, t <: Adt3[n]](x: t, y: t) = {}
  add3(Adt3.High(), Adt3.High())
  // add3(Adt3.High(), Adt3.Low()) // fail,ok

  type Shape[width <: Int, bits <: Adt1]

  def sp1(sp: Shape[1, Adt1.High.type]) = {}

// Only allowed values are Min <= N <= Max.
  type Bounded[MIN <: Int, MAX <: Int] <: Int = MAX match
    case MIN => MIN
    case _   => MAX | Bounded[MIN, MAX - 1]

  // val zero15: Bounded[1, 5] = 0 // ERROR
  val one15: Bounded[1, 5] = 1
  val two15: Bounded[1, 5] = 2
}
