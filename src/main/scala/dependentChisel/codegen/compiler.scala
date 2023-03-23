package dependentChisel.codegen

import com.doofin.stdScalaCross.*

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.global

import seqCmdTypes.*
import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.algo.seqCmd2tree.*

import firrtlTypes.*

import dependentChisel.codegen.seqCmdTypes
object compiler {

  /** chisel ModLocalInfo to FirrtlModule(IO bundle,AST for the circuit) */
  def chiselMod2firrtlCircuits(chiselMod: UserModule) = {
    val modInfo = chiselMod.modLocalInfo
    val glob = chiselMod.globalInfo
    val mainModuleName = modInfo.classNm

    FirrtlCircuit(mainModuleName, glob.modules.toList map chiselMod2firrtlMod)
  }

  def chiselMod2firrtlMod(chiselMod: UserModule): FirrtlModule = {
    val modInfo = chiselMod.modLocalInfo
    val cmds = modInfo.commands
    val anf = cmdListToSingleAssign(cmds.toList)
    val tree: AST = list2tree(anf)
    FirrtlModule(modInfo.classNm, modInfo.io.toList, tree)
  }

  def firrtlCircuits2str(fCircuits: FirrtlCircuit): String = {
    val modStr = fCircuits.modules map (m => firrtlModule2str(m, " "))
    s"circuit ${fCircuits.mainModuleName} : \n" + modStr.mkString("\n")
  }

  def firrtlModule2str(fMod: FirrtlModule, indent: String = ""): String = {
    val circuitStr = tree2firrtlStr(fMod.ast, indent)
    val ioInfoStr = fMod.io.reverse // looks better
      .map { (x: IOdef) =>
        val prefix = x.tpe match {
          case "input"  => "flip"
          case "output" => ""
        }
        val name = fullName2IOName(x.name) // rm module name
        s"$prefix $name : UInt<${x.width.getOrElse(-1)}>"
      }
      .mkString(", ")

    val modIOstr =
      s"${indent}output io : { ${ioInfoStr}}\n"

    s"""${indent}module ${fMod.name} :
  ${indent}input clock : Clock
  ${indent}input reset : UInt<1>\n  """ +
      modIOstr + circuitStr
  }

  /** print AST to indent firrtl */
  def tree2firrtlStr(tr: AST, indent: String = ""): String = {
    val nodeStr: String = tr.value match {
      case x: Ctrl =>
        x match {
          case Ctrl.If(b)  => s"when ${expr2firrtlStr(b)} :"
          case Ctrl.Else() => "else "
          case Ctrl.Top()  => ""
        }
      case stmt: FirStmt => stmt2firrtlStr(stmt)
    }

    indent + nodeStr + (tr.cld map (cld =>
      "\n" + tree2firrtlStr(cld, indent + "  ")
    )).mkString
  }

  /** rm module names from io name */
  def fullName2IOName(ioName: String, withIOprefix: Boolean = false): String = {
    (if withIOprefix then "io." else "") + ioName.split('.').last
  }

  def expr2firrtlStr(expr: Expr[?]): String = {
    expr match {
      case BinOp(a, b, nm) =>
        val opName = firrtlOpMap.find(_._1 == nm).map(_._2).getOrElse(nm)
        s"$opName(${expr2firrtlStr(a)},${expr2firrtlStr(b)})" // here it's SSA form

      case x: Var[?] =>
        fullName2IOName(x.getname, withIOprefix = x.getIsIO)
      case Lit(i)         => i.toString()
      case BoolExpr(expr) => expr2firrtlStr(expr)
    }
  }
  def stmt2firrtlStr(stmt: FirStmt) = {
    val FirStmt(lhs, op, rhs, prefix) = stmt
    val opName = firrtlOpMap.find(_._1 == op).map(_._2).getOrElse(op)
    prefix + expr2firrtlStr(lhs) + s" $opName ${expr2firrtlStr(rhs)}"
  }

  /** convert expr to stmt bind: turn a+b into gen_ = a+b */
  def expr2stmtBind(a: Expr[?]) = {
    val newValue = "g_" + global.getUid
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
