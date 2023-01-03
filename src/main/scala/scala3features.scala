object scala3features {

  /** Enum Types: http://dotty.epfl.ch/docs/reference/enums/adts.html
    */
  enum ListEnum[+A] {
    case Cons(h: A, t: ListEnum[A])
    case Empty
  }

  enum Planet(mass: Double, radius: Double) {
    private final val G = 6.67300e-11
    def surfaceGravity = G * mass / (radius * radius)
    def surfaceWeight(otherMass: Double) = otherMass * surfaceGravity

    case Mercury extends Planet(3.303e+23, 2.4397e6)
    case Venus extends Planet(4.869e+24, 6.0518e6)
    case Earth extends Planet(5.976e+24, 6.37814e6)
  }
  def test1 = {

    if true then "true" else "false"

    val tup1 = (1, 2, "str")
    tup1.splitAt(1)
    tup1.size
    val tupTail = tup1.tail
    val toList = (1, 'a', 2).toList
    val natTrans = (1, 'a').map[[X] =>> Option[X]]([T] => (t: T) => Some(t))
  }
}
