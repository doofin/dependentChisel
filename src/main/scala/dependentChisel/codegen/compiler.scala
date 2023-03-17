package dependentChisel.codegen

import com.doofin.stdScalaCross.*

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.global

import seqCmds.*
import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.algo.seqCmd2tree.*

import firrtlTypes.*

object compiler {

  /** chisel ModLocalInfo to FirrtlModule(IO bundle,AST for the circuit) */
  def chiselMod2firrtlModule(chiselMod: UserModule): FirrtlModule = {
    val modInfo = chiselMod.modLocalInfo

    val cmds = modInfo.commands
    val anf = cmdListToSingleAssign(cmds.toList)
    val tree: AST = list2tree(anf)
    FirrtlModule(modInfo.classNm, modInfo.io.toList, tree)
  }

  def firrtlModule2str(fMod: FirrtlModule): String = {
    val circuitStr = tree2firrtlStr(fMod.ast)
    val ioInfoStr = fMod.io
      .map { x =>
        val prefix = x.tpe match {
          case "input"  => "flip"
          case "output" => ""
        }
        s"$prefix ${x.name} : UInt<${x.width.getOrElse(-1)}>"
      }
      .mkString(", ")

    val modIOstr =
      s"output io : { ${ioInfoStr}}\n"
    s"""module ${fMod.name} :
  input clock : Clock
  input reset : UInt<1>\n  """ +
      modIOstr + circuitStr
  }

  /** print AST to indent firrtl */
  def tree2firrtlStr(tr: AST, indent: String = ""): String = {
    val valStr: String = tr.value match {
      case x: Ctrl =>
        x match {
          case Ctrl.If(b)  => "when " + b.toString()
          case Ctrl.Else() => "else "
          case Ctrl.Top()  => ""
        }
      case stmt @ FirStmt(lhs, op, rhs, prefix) => stmt.toString()
    }

    indent + valStr + (tr.cld map (cld =>
      "\n" + tree2firrtlStr(cld, indent + "  ")
    )).mkString // ("\n")
  }

  /** convert expr to stmt bind: turn a+b into gen_ = a+b */
  def expr2stmtBind(a: Expr[?]) = {
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
  def stmtToSingleAssign(
      stmt: FirStmt,
      resList: List[FirStmt] = List()
  ): List[FirStmt] = {
    val FirStmt(lhs, op, rhs, _) = stmt

    /* if lhs is io,change := to <= and make new conn
    io.y:=a+b becomes y0=a+b;io.y<=y0
     */

    // dbg(x)

    rhs match {
      // fresh stmt for the first 2 case
      case bop @ BinOp(a: BinOp[?], b, nm) => // bin expr like a+b+c
        val genStmt = expr2stmtBind(a)
        stmtToSingleAssign(
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
      case x: Expr[?] => // bin expr like a+b or others
        // println(x)
        val genStmt = expr2stmtBind(rhs)
        val stmtNew = lhs match {
          case Input(name) =>
            List(genStmt, stmt.copy(op = "<=", rhs = genStmt.lhs))
          case Output(name) =>
            List(genStmt, stmt.copy(op = "<=", rhs = genStmt.lhs))
          case VarLit(name) => List(stmt)
        }
        stmtNew ++ resList
    }
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
