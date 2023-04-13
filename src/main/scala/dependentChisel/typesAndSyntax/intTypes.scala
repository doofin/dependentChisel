package dependentChisel.typesAndSyntax

/** Wire, Reg, and IO */
object intTypes {
  /* Chisel provides three data types to describe connections, combinational logic, and
registers: Bits, UInt, and SInt. UInt and SInt extend Bits, and all three types
represent a vector of bits */

// may only need UINT for serial port example(although it needs Bool type )
  enum bt2 { case u, s, b }

  trait ctype[width <: Int, b <: bt2]
  case class u1[width <: Int]() extends ctype[width, bt2.u.type]
  case class s1[width <: Int]() extends ctype[width, bt2.s.type]

  trait ExprB[idx <: bt2, width <: Int, b <: ctype[width, idx]] {
    // def +(oth: ExprB[idx, width, b]) = { "ok!!" }
  }

  def add2[idx <: bt2, width <: Int, b <: ctype[width, idx]](
      x: b,
      y: b
  ) = {}

  add2(u1[1](), u1[1]())

}
