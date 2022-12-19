/*partial DependentTypes via compile time inline and checks */
import scala.compiletime.ops.int.*
import scala.compiletime.ops.int.S

object depTypes {
  sealed trait DTList[N <: Int]: // N is a singleton subtype of Int
    inline def size: N = valueOf[N] // <2>

    def +:[H <: Matchable](h: H): DTNonEmptyList[N, H, this.type] = // <3>
      DTNonEmptyList(h, this)

  case object DTNil extends DTList[0] // <4>

  case class DTNonEmptyList[N <: Int, H <: Matchable, T <: DTList[N]]( // <5>
      head: H,
      tail: T
  ) extends DTList[S[N]]

// https://github.com/MaximeKjaer/tf-dotty/blob/master/modules/compiletime/src/main/scala/io/kjaer/compiletime/dependent.scala
// defines arithmetic on type
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
  inline def wire[X <: Int]: X + 2 = {
    val singV = valueOf[X]
    singV.add(2)
  }

  case class wireTp[X <: Int](x: X)
  inline def wireConn[X <: Int](x: wireTp[X], y: wireTp[X]) = {}

  inline def wireNew[X <: Int]
      : wireTp[X + 2] = { // must use inline for  valueOf!
    val singV = valueOf[X]
    wireTp(singV.add(2))
  }

// misc check
  def wireId[X <: Int](x: X): X = { x }
  def wireId2(x: Int) = { x } // won't work for type anno
  def wire2[X <: Int](x: X) = { x add 2 }
  // def wireSuc[X <: Int](x: X): S[X] = { x + 1 } //fail
  def wire0: Rank[0] = 0
// def wire2[X <: Int] = { valueOf(X).add(2) }

  def run = {
    val list = 1 +: 2.2 +: DTNil

    // 1.add(2): 3
    println(list)
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
    wire[4]: 6 // ok
    wireConn(wireNew[1], wireNew[1]) // ok
    // wireConn(wireNew[1], wireNew[2]) // fail
  }

}
