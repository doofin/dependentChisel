// src/script/scala/progscala3/typesystem/deptypes/DependentTypes.scala
import scala.compiletime.ops.int.*

object DependentTypes {
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
  }
  /*
  https://github.com/MaximeKjaer/tf-dotty/blob/master/modules/compiletime/src/main/scala/io/kjaer/compiletime/Shape.scala */
  def run = {
    val list = 1 +: 2.2 +: DTNil
    println(list)
    1.add(2)
  }

  /* list.size
list.head
list.tail
list.tail.size
list.tail.head
list.tail.tail
list.tail.tail.size

list.tail.tail.head      // ERROR
list.tail.tail.tail      // ERROR

DTNil.size
DTNil.head               // ERROR - "head is not a member of ..."
DTNil.tail               // ERROR - "tail is not a member of ..."
   */ // end::usage[]
}
// values as type parameter
