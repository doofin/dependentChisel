package dependentChisel.codegen

import dependentChisel.typesAndSyntax.basicTypes.*
import com.doofin.stdScalaCross.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.global

import seqCmds.*
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
  type Uid = String
  type CmdName = String
  // type CmdTup[w <: Int] = (Uid, Bool[w])

  /* sealed trait Cmds
  case class Block(s: String) extends Cmds // control structure
  // case class Start() extends Cmds
  case class FirStmt(
      lhs: Var[?],
      op: String,
      rhs: Expr[?],
      prefix: String = ""
  ) extends Cmds // prefix can be node */

  // add this duplicate rm the cyclic ref bug below?
  /* case class FirStmt22(
      lhs: String,
      op: String,
      rhs: Expr[?],
      indentation: String = "",
      prefix: String = "" // prefix can be node
  ) extends Cmds  */

  case class fModule(io: IODef, cmds: List[Cmds])
  case class fCircuits(mod: List[fModule], mainMod: String)

  /** convert expr to stmt bind: turn a+b into gen_ = a+b */
  def expr2stmtBind(a: BinOp[?]) = {
    val newValue = "gen_" + global.getUid
    FirStmt(VarLit(newValue), ":=", a, prefix = "node ")
  }
  /* to A-normal form
  https://en.wikipedia.org/wiki/A-normal_form

  convert y=a+b+c
  to
    y0=a+b
    y=y0+c
  stmt-> list stmt
   */
  def toANF(
      stmt: FirStmt,
      resList: List[FirStmt] = List()
  ): List[FirStmt] = {
    val x = stmt.rhs
    // dbg(x)

    x match {
      // fresh stmt for the first 2 case
      case bop @ BinOp(a: BinOp[?], b, nm) =>
        val genStmt = expr2stmtBind(a)
        toANF(
          genStmt,
          List(
            FirStmt(
              stmt.lhs,
              ":=",
              bop.copy(a = VarLit(genStmt.lhs.getname)),
              prefix = "node "
            )
          ) ++ resList
        )
      case x: Expr[?] =>
        // println(x)
        stmt +: resList
    }
  }

  def ioTransformRhs[w <: Int](
      expr: Expr[w]
  ): Expr[w] = {
    // if lhs is io,always make a fresh node
    /* val lhs = stmt.lhs.match {
      case Output(name) =>
        VarLit(s"io.$name")
      // case VarLit(name) =>
      // case Input(name)  =>
      case x => x
    } */

    val r = expr match {
      case Input(name)  => VarLit(name)
      case Output(name) => VarLit(name)
      // case VarLit(name)    =>
      case BinOp(a, b, nm) => BinOp(ioTransformRhs(a), ioTransformRhs(b), nm)
      // case Bool(a, b)      =>
      // case Lit(i)          =>
      case x => x
    }
    // dbg(r)
    r
  }

  /* list of stmt-> toANF->genFirrtlStmt tree or dag?->firrtl str */
  def genFirrtlStmt(x: List[Cmds], indent: String = "") = {
    val stmtList = x flatMap {
      case stmt @ FirStmt(lhs, op, rhs, prefix) =>
        // rhs is sure to be binOp now after ANF
        // dbg(lhs, rhs)
        lhs match {
          // if lhs is io,always make a fresh node
          case Output(name) =>
            rhs match {
              case bo @ BinOp(a, b, nm) =>
                val newStmt =
                  expr2stmtBind(
                    ioTransformRhs(bo).asInstanceOf[BinOp[?]]
                  ) // todo too ugly
                List(
                  newStmt,
                  FirStmt(
                    lhs,
                    "<=",
                    VarLit(newStmt.lhs.getname)
                  )
                )
              // case x @ VarLit(name) =>
              // case Bool(a, b)       =>
              // case Input(name)      =>
              // case Output(name)     =>
              // case Lit(i)           =>
              case _ =>
                println("shouldn't happen!")
                List(stmt.copy(rhs = ioTransformRhs(stmt.rhs)))
            }
          // case VarLit(name) =>
          // case Input(name)  =>
          case _ => List(stmt)
        }

      case x: Block => List(x)
    }
    stmtList
  }

  def genFirrtlStr(x_ : List[Cmds]) = {
    val x = genFirrtlStmt(x_)
    val strList = x map {
      case st @ FirStmt(lhs, op, rhs, prefix) =>
        // dbg(st)
        // val op_f = "<="
        val lhs_f = "io." + lhs.getname.split('.').last
        // dbg(lhs)
        val rhsNm = rhs match {
          case VarLit(name) => name
          case Lit(i)       => i.toString()
          // case BinOp(a, b, nm) =>
          // case Bool(a, b)      =>
          // case Input(name)     =>
          // case Output(name)    =>
          case x => x.toString()
        }
        s" $prefix ${lhs_f} $op ${rhsNm}"

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
