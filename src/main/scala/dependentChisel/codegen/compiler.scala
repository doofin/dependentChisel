package dependentChisel.codegen

import com.doofin.stdScalaCross.*

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.global

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.algo.seqCmd2tree.*

import seqCommands.*
import firrtlTypes.*

object compiler {

  /** convert chiselMod to str */
  def chiselMod2str(chiselMod: UserModule) = {
    firrtlCircuits2str(chiselMod2firrtlCircuits(chiselMod))
  }

  /** chisel ModLocalInfo to FirrtlModule(IO bundle,AST for the circuit) */
  def chiselMod2firrtlCircuits(chiselMod: UserModule) = {
    val modInfo = chiselMod.modLocalInfo
    val glob = chiselMod.globalInfo
    val mainModuleName = modInfo.className

    FirrtlCircuit(mainModuleName, glob.modules.toList map chiselMod2firrtlMod)
  }

  private def chiselMod2firrtlMod(chiselMod: UserModule): FirrtlModule = {
    val modInfo: ModLocalInfo = chiselMod.modLocalInfo
    val anf = cmdListToSingleAssign(modInfo.commands.toList)
    val ioNameChanged = cmdsTransform(modInfo.instName, anf)
    val tree: AST = list2tree(ioNameChanged)
    FirrtlModule(modInfo, modInfo.io.toList, tree)
  }

  /** whole firrtl Circuit AST to serialized str format */
  def firrtlCircuits2str(fCircuits: FirrtlCircuit): String = {
    val modStr = fCircuits.modules map (m => firrtlModule2str(m, " "))
    s"circuit ${fCircuits.mainModuleName} : \n" + modStr.mkString("\n")
  }

  /** for only one firrtl module */
  private def firrtlModule2str(
      fMod: FirrtlModule,
      indent: String = ""
  ): String = {
    val circuitStr = tree2firrtlStr(fMod.ast, indent)
    val instName: String = fMod.modInfo.instName // name changes for io
    val ioInfoStr = fMod.io.reverse // looks better
      .map { (x: IOdef) =>
        val prefix = x.tpe match {
          case "input"  => "flip"
          case "output" => ""
        }
        // dbg(x.name)
        val name = x.name // fullName2IOName(instName, x.name) // rm module name
        // pt(instName, x.name, name)
        s"$prefix $name : UInt<${x.width.getOrElse(-1)}>"
      }
      .mkString(", ")

    val modIOstr =
      s"${indent}output io : { ${ioInfoStr}}\n"

    s"""${indent}module ${fMod.modInfo.className} :
  ${indent}input clock : Clock
  ${indent}input reset : UInt<1>\n  """ +
      modIOstr + circuitStr
  }

  def splitName(fullName: String) = {
    val instName :: name :: Nil = fullName.split('.').toList: @unchecked
    (instName, name)
  }

  /** print AST to indent firrtl */
  def tree2firrtlStr(tr: AST, indent: String = ""): String = {
    val nodeStr: String = tr.value match {
      case x: Ctrl =>
        indent + (x match {
          case Ctrl.If(b)  => s"when ${expr2firrtlStr(b)} :"
          case Ctrl.Else() => "else "
          case Ctrl.Top()  => ""
        })
      case stmt: FirStmt     => indent + stmt2firrtlStr(stmt)
      case stmt: NewInstStmt => newInstStmt2firrtlStr(indent, stmt)
      case stmt: VarDecls =>
        indent + varDecl2firrtlStr(stmt)
    }

    nodeStr + (tr.cld map (cld =>
      "\n" + tree2firrtlStr(cld, indent + "  ")
    )).mkString
  }

  /** rm module or instance names from io name, for usage in gen firrtl io
    * section
    */
  def fullName2IOName(
      instName: String,
      fullName: String
      // withIOprefix: Boolean = false
  ): String = {
    /*     if fullName.contains(".")
    then (if withIOprefix then "io." else "") + fullName.split('.').last
    else fullName
     */

    if fullName.contains(".") then
      val (instNameSplit, name) = splitName(fullName)
      if instNameSplit == instName then "io." + name
      else fullName
    else fullName
  }

  def expr2firrtlStr(expr: Expr[?]): String = {
    expr match {
      case BinOp(a, b, nm) =>
        val opName = firrtlOpMap.find(_._1 == nm).map(_._2).getOrElse(nm)
        s"$opName(${expr2firrtlStr(a)},${expr2firrtlStr(b)})" // here it's SSA form

      case x: Var[?] =>
        // fullName2IOName(x.getname, withIOprefix = x.getIsIO)
        // ioTransformOrSkip(x).getname
        x.getname
      case Lit(i)         => i.toString()
      case BoolExpr(expr) => expr2firrtlStr(expr)
    }
  }

  def stmt2firrtlStr(stmt: FirStmt) = {
    val FirStmt(lhs, op, rhs, prefix) = stmt
    val opName = firrtlOpMap.find(_._1 == op).map(_._2).getOrElse(op)
    prefix + expr2firrtlStr(lhs) + s" $opName ${expr2firrtlStr(rhs)}"
  }

  def newInstStmt2firrtlStr(indent: String, stmt: NewInstStmt) = {
    /*     m1.clock <= clock
    m1.reset <= reset */
    Seq(
      s"inst ${stmt.instNm} of ${stmt.modNm}",
      s"${stmt.instNm}.clock <= clock",
      s"${stmt.instNm}.reset <= reset"
    ).map(x => indent + x)
      .mkString("\n")
  }

  def varDecl2firrtlStr(stmt: VarDecls) = {
    val VarDymTyped(width: Int, tp: VarDeclTp, name: String) = stmt.v
    tp match {
      case VarDeclTp.Reg =>
        /* reg mReg : UInt<16>, clock with :
      reset => (reset, UInt<16>("h0")) @[regWire.scala 13:21] */

        s"""reg ${name} : UInt<${width}>, clock with : reset => (reset, UInt<${width}>("h0"))"""
      case VarDeclTp.Wire =>
        /* wire wire1 : UInt<16> @[regWire.scala 18:19] */
        s"wire $name : UInt<$width>"

      // io is only put in header section like output io : { flip a : UInt<16>, flip b : UInt<16>, y : UInt<16>}
      case VarDeclTp.Input  => ""
      case VarDeclTp.Output => ""
    }
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
        val genStmt = expr2stmtBind(rhs)
        val stmtNew = lhs match {
          /* if lhs is IO,change := to <= and make new conn
        io.y:=a+b becomes y0=a+b;io.y<=y0
           */

          case x: (Input[?] | Output[?]) =>
            List(genStmt, stmt.copy(op = "<=", rhs = genStmt.lhs))
          case VarDymTyped(width, tp, name) =>
            tp match {
              case VarDeclTp.Input | VarDeclTp.Output =>
                List(genStmt, stmt.copy(op = "<=", rhs = genStmt.lhs))
              case _ => List(stmt)
            }

          case _ => List(stmt)
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

  /** var to var Transform (for io): modify names for io
    */
  def varNameTransform(thisInstName: String, v: Var[?]): Var[?] = {
    v match {
      case x @ VarLit(name)                 => x
      case x @ VarDymTyped(width, tp, name) =>
        // dbg("VarDymTyped")
        tp match {
          case VarDeclTp.Input =>
            x.copy(name = ioNameTransform(thisInstName, name))
          case VarDeclTp.Output =>
            x.copy(name = ioNameTransform(thisInstName, name))
          // case VarDeclTp.Reg  =>
          // case VarDeclTp.Wire =>
          case _ => x
        }
      case x: Input[?] =>
        x.copy(name = ioNameTransform(thisInstName, x.name))

      case x: Output[?] =>
        x.copy(name = ioNameTransform(thisInstName, x.name))
    }
  }

  /** modify names for io: check if instantiated instance have same name, if so
    * refer to it by io.a, otherwise add inst name as prefix
    */
  def ioNameTransform(thisInstName: String, ioFullName: String) = {
    val instName :: name :: Nil = ioFullName.split('.').toList: @unchecked
    // rm inst name if refer in same mod
    val pfx = if thisInstName == instName then "io" else s"$instName.io"
    // dbg(pfx)
    s"$pfx.$name"
  }

  /** expr to expr Transform: */
  def exprTransform(thisInstName: String, e: Expr[?]): Expr[?] = {
    e match {
      case v: Var[?] => varNameTransform(thisInstName, v)
      case x: BinOp[w] =>
        BinOp(
          exprTransform(thisInstName, x.a).asInstanceOf[Expr[Nothing]],
          exprTransform(thisInstName, x.b).asInstanceOf[Expr[Nothing]],
          x.nm
        )
      case x => x
    }
  }

  /** modify names for io after cmdListToSingleAssign
    */
  def cmdsTransform(thisInstName: String, cmdList: List[Cmds]): List[Cmds] = {
    cmdList map {
      case x @ FirStmt(lhs, op, rhs, prefix) =>
        val newStmt = x.copy(
          lhs = varNameTransform(thisInstName, lhs),
          rhs = exprTransform(thisInstName, rhs)
        )
        // dbg(newStmt)
        newStmt
      case x => x
    }
  }
}
