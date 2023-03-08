package dependentChisel.codegen

import dependentChisel.typesAndSyntax.basicTypes.*
import com.doofin.stdScalaCross.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.global

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

  /* enum Cmds {
  } */
  // object Cmds {}
  sealed trait Cmds
  case class Block(s: String) extends Cmds // control structure

  // add this duplicate rm the cyclic ref bug below?
  /*   case class FirStmt22(
      lhs: String,
      op: String,
      rhs: Expr[?],
      // rhs: String,
      indentation: String = "",
      prefix: String = ""
  ) extends Cmds // prefix can be node */

  case class FirStmt(
      lhs: String,
      op: String,
      rhs: Expr[?],
      // rhs: String,
      indentation: String = "",
      prefix: String = ""
  ) extends Cmds // prefix can be node

  case class fModule(io: IODef, cmds: List[Cmds])
  case class fCircuits(mod: List[fModule], mainMod: String)

  /* to A-normal form
  https://en.wikipedia.org/wiki/A-normal_form

  convert y=a+b+c
  to
    y0=a+b
    y=y0+c
  stmt-> list stmt
   */
  def genFresh(a: BinOp[?]): (String, FirStmt) = {
    // val uuid =
    // java.util.UUID.randomUUID.toString // System.currentTimeMillis().toString()
    val newValue = "gen_" + global.getUid
    (newValue, FirStmt(newValue, ":=", a))
  }
  def toANF(
      stmt: FirStmt,
      resList: List[FirStmt] = List()
  ): List[FirStmt] = {
    val x = stmt.rhs
    dbg(x)

    x match {
      // fresh stmt for the first 2 case
      case bop @ BinOp(a: BinOp[?], b, nm) =>
        val (fresh, genStmt) = genFresh(a)
        toANF(
          genStmt,
          List[FirStmt](
            FirStmt(stmt.lhs, ":=", bop.copy(a = VarLit(fresh)))
          ) ++ resList
        )
      // case BinOp(a, b: BinOp[?], nm) => toANF(a, genFresh(b) +: res)
      // case Input(nm)  => res
      // case Output(nm) => res
      // case Lit(i)     => res
      // case BinOp(a, b, nm)           =>
      case x: Expr[?] =>
        println(x)
        stmt +: resList
    }
  }

  def toSSA(
      init: List[FirStmt],
      x: Expr[_]
  ): Either[List[FirStmt], Expr[_]] = {
    x match {
      case BinOp(a, b, nm) =>
        (toSSA(init, a), toSSA(init, b)).match
          case (Left(xs), Left(ys)) => Left(init ++ xs ++ ys)
          case (Left(xs), Right(e)) => toSSA(init ++ xs, e)
      // case (Right(e), Right(e2)) => toSSA(init ++ xs, e)
      // to ssa recursively
      case x => Right(x)
    }
  }

  def gen(x: List[Cmds]) = {
    val strList = x map {
      case FirStmt(lhs, op, rhs, indentation, prefix) =>
        val op_f = op match {
          case ":=" => "<="
        }
        val lhs_f = "io." + lhs.split('.').last
        // dbg(lhs)
        s"$indentation ${lhs_f} $op_f ${rhs}"

      case x: Block => x.s
    }

    strList.mkString("\n")
  }

  /*   //  simplified
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
   */
}
