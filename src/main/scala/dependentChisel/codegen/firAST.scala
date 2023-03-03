package dependentChisel.codegen

object firAST {
  //  simplified
  val adderSimp =
    """circuit adder1 :
      |  module adder1 :
      |    input clock : Clock
      |    input reset : UInt<1>
      |    output io : { flip a : UInt<16>, flip b : UInt<16>, y : UInt<16>}
      |
      |    node _io_y_T = add(io.a, io.b) @[adder.scala 12:16]
      |    io.y <= _io_y_T @[adder.scala 12:8]
      |""".stripMargin

  case class IODef(io: List[String])
  enum Cmds {
    case Stmt(
        lhs: String,
        op: Expr,
        rhs: Expr,
        indentation: String = "",
        prefix: String = ""
    ) // prefix can be node
    case Expr()
  }
  case class fModule(io: IODef, cmds: List[Cmds])
  case class fCircuits(mod: List[fModule], mainMod: String)

  def a(x: Cmds) = {}
}
