package dependentChisel.codegen

import scala.compiletime.*

import com.doofin.stdScalaCross.*

import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.global

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.algo.seqCmd2tree.*

import seqCommands.*
import firrtlTypes.*

import scala.util.*
import scala.collection.mutable
import dependentChisel.firrtlUtils

object compiler {

  /** convert chiselMod to str */
  def chiselMod2str(chiselMod: UserModule) = {
    firrtlCircuits2str(chiselMod2firrtlCircuits(chiselMod))
  }

  def chiselMod2verilog(
      mod: UserModule,
      printVerilog: Boolean = false,
      printCmdList: Boolean = false
  ) = {
    val fMod = chiselMod2firrtlCircuits(mod, printCmdList)
    val firCirc = firrtlCircuits2str(fMod)
    val verilog = firrtlUtils.firrtl2verilog(firCirc)
    if printVerilog then println(verilog)
    verilog
  }

  /** chisel ModLocalInfo to FirrtlModule(IO bundle,AST for the circuit) */
  def chiselMod2firrtlCircuits(chiselMod: UserModule, printCmdList: Boolean = false) = {
    val modInfo: ModLocalInfo = chiselMod.modLocalInfo
    val allMods: List[UserModule] = chiselMod.globalInfo.modules.toList

    val typeMap =
      allMods.map(_.modLocalInfo.typeMap) reduce (_ ++ _)

    val mainModuleName = modInfo.className

    FirrtlCircuit(mainModuleName, allMods map chiselMod2firrtlMod(typeMap, printCmdList))
  }

  /** contains width assert if global.enableWidthCheck */
  def checkWidthAssert(
      typeMap: mutable.Map[Expr[?], Int],
      cmdList: List[Cmds]
  ) = {
    var checkWidthVar = true
    val r = cmdList.collect {
      case x: AtomicCmds =>
        if (!typeCheck.checkCmdWidth(typeMap, x)) {
          checkWidthVar = false
        }
        x
      case x => x
    }
    if (global.enableWidthCheck) assert(checkWidthVar, "checkWidth failed!")
    r
  }

  private def chiselMod2firrtlMod(
      typeMap: mutable.Map[Expr[?], Int],
      printCmdList: Boolean
  )(chiselMod: UserModule): FirrtlModule = {
    val modInfo: ModLocalInfo = chiselMod.modLocalInfo
    // pp(modInfo.typeMap)
    val cmdList = modInfo.commands.toList
    if printCmdList then pp(modInfo.commands.toList)

    val cmds_widthChk: List[Cmds] = checkWidthAssert(typeMap, cmdList)

    val cmds_ANF: List[Cmds] = expandCmdList(cmds_widthChk)
    // dbg(cmds_ANF)
    val ioNameChanged: List[Cmds] = cmdsTransform(modInfo.thisInstanceName, cmds_ANF)
    // dbg(ioNameChanged)
    val tree: AST = list2tree(ioNameChanged)
    FirrtlModule(modInfo, modInfo.io.toList, tree)
  }

  /** whole firrtl Circuit AST(several modules) to serialized str format */
  def firrtlCircuits2str(fCircuits: FirrtlCircuit): String = {
    val modStr = fCircuits.modules map (m => firrtlModule2str(m, " "))
    s"circuit ${fCircuits.mainModuleName} : \n" + modStr.mkString("\n\n")
  }

  /** for only one firrtl module */
  private def firrtlModule2str(
      fMod: FirrtlModule,
      indent: String = ""
  ): String = {
    // val skip = "\n" + indent + "  " + "skip"
    val skip = ""
    val circuitStr = tree2firrtlStr(fMod.ast, indent) + skip
    // println("circuitStr.isEmpty:" + circuitStr.trim().isEmpty())
    // pp(circuitStr)
    val instName: String = fMod.modInfo.thisInstanceName // name changes for io
    val ioInfoStr = fMod.io.reverse // looks better
      .map { (x: IOdef) =>

        val prefix = x.tpe match {
          case VarType.Input  => "flip"
          case VarType.Output => ""
        }

        // dbg(x.name)
        assert(x.width.nonEmpty, "x.width shouldn't be empty")
        val name = x.name // fullName2IOName(instName, x.name) // rm module name
        // pt(instName, x.name, name)
        s"$prefix $name : UInt<${x.width.get}>"
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

  /** can insert more commands */
  def expandCmdList(cmdList: List[Cmds]): List[Cmds] = {
    cmdList flatMap {
      case x: FirStmt =>
        val fir = stmtToSingleAssign(x)
        // dbg(fir)
        fir
      case orig @ Start(ctrl: Ctrl, uid) =>
        ctrl match {
          case ctrlIf @ Ctrl.If(bool) =>
            val anf_stmts: List[FirStmt] =
              stmtToSingleAssign(expr2stmtBind(bool))
            val anf_res =
              anf_stmts :+ orig.copy(ctrl =
                ctrlIf.copy(cond = anf_stmts.last.lhs.asTypedUnsafe[1])
              )
            // dbg(anf_res)
            anf_res
          case _ => List(orig) // bug! will eat "else"
        }
      case x => List(x)
    }
  }

  /** print AST to indent firrtl */
  def tree2firrtlStr(tr: AST, indent: String = ""): String = {
    val nodeStr: String = tr.value match {
      case x: Ctrl =>
        indent + (x match {
          case Ctrl.If(b)  => s"when ${expr2firrtlStr(b)} :"
          case Ctrl.Else() => "else :"
          case Ctrl.Top()  => ""
        })
      case stmt: FirStmt     => indent + stmt2firrtlStr(stmt)
      case stmt: NewInstStmt => newInstStmt2firrtlStr(indent, stmt) + "\n"
      case stmt: VarDecls =>
        indent + varDecl2firrtlStr(indent, stmt)
    }

    nodeStr + (tr.cld map (cld => "\n" + tree2firrtlStr(cld, indent + "  "))).mkString
  }

  /** rm module or instance names from io name, for usage in gen firrtl io section
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
      case UniOp(a, opName) =>
        s"$opName(${expr2firrtlStr(a)})"
      case x: Var[?] =>
        // dbg(x)
        x.getname
      case Lit(w) =>
        // h0 means HexLit of 0
        s"""UInt<${w}>("$w")"""
        ???
      case LitDym(i, width) =>
        val (hex, _) = int2hexAndCeiling(i)
        s"""UInt<${width}>("h$hex")"""
      // case ExprAsBool(expr) => expr2firrtlStr(expr)
    }
  }

  /** Compute the log2 of a Scala integer, rounded up. Useful for getting the number of
    * bits needed to represent some number of states (in - 1). To get the number of bits
    * needed to represent some number n, use log2Ceil(n + 1). Note: can return zero, and
    * should not be used in cases where it may generate unsupported zero-width wires.
    * @example
    *   {{{ log2Ceil(1) // returns 0 log2Ceil(2) // returns 1 log2Ceil(3) // returns 2
    *   log2Ceil(4) // returns 2 }}}
    */
  object log2Ceil {
    // (0 until n).map(_.U((1.max(log2Ceil(n))).W))
    def apply(in: BigInt): Int = {
      require(in > 0)
      (in - 1).bitLength
    }
    def apply(in: Int): Int = apply(BigInt(in))
  }

  def int2hexAndCeiling(i: Int) = {
    // val ceiling = (math.log(i) / math.log(2)).ceil.toInt

    if i == 0 then ("0", 1)
    else {
      val ceilingWidth = 1 max log2Ceil(i) // 1.max(log2Ceil(n))
      (i.toHexString, ceilingWidth)
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

  def varDecl2firrtlStr(indent: String = "", stmt: VarDecls) = {
    val VarDymTyped(width: Int, tp: VarType, name: String) = stmt.v
    tp match {
      case VarType.Reg => // reg without init
        /* reg mReg : UInt<16>, clock with :
      reset => (reset, UInt<16>("h0")) @[regWire.scala 13:21] */

        s"""reg ${name} : UInt<${width}>, clock with : 
        ${indent}reset => (reset, UInt<${width}>("h0"))"""

      case VarType.RegInit(init) =>
        /* reg mReg : UInt<16>, clock with :
      reset => (reset, UInt<16>("h0")) @[regWire.scala 13:21] */

        s"""reg ${name} : UInt<${width}>, clock with : 
        ${indent}reset => (reset, ${expr2firrtlStr(init)})"""

      case VarType.Wire =>
        /* wire wire1 : UInt<16> @[regWire.scala 18:19] */
        s"wire $name : UInt<$width>"

      /* io is only put in header section like output io : { flip a : UInt<16>, flip b : UInt<16>, y : UInt<16>}
      so no need to deal with it */
      case VarType.Input  => ""
      case VarType.Output => ""
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
    val FirStmt(stmtLhs, op, stmtRhs, _) = stmt

    stmtRhs match {
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

      /* for uniary operators:
      convert
      a = not b --->
      node n0=not b
      a=n0
       */
      case uop @ UniOp(a, nm) =>
        val genStmt = expr2stmtBind(a)
        val stmtNew = FirStmt(
          stmtLhs,
          ":=",
          genStmt.lhs
        )

        stmtToSingleAssign(
          genStmt,
          stmtToSingleAssign(stmtNew) // convert := to <= for IO
            ++ resList
        )
      // other expr like a+b or others, just ...
      case x: Expr[?] =>
        IOassignTransform(stmt) ++ resList
    }
  }

  /** if lhs is IO,change := to <= and make new conn io.y:=a+b becomes y0=a+b;io.y<=y0 new
    * : don't do above
    */
  def IOassignTransform(stmt: FirStmt): List[FirStmt] = {
    stmt.lhs match {
      /* if lhs is IO,change := to <= and make new conn
        io.y:=a+b becomes y0=a+b;io.y<=y0
        new : don't do above
       */
      case x: (VarTyped[?] | VarDymTyped) =>
        val genStmt = expr2stmtBind(stmt.rhs)
        List(genStmt, stmt.copy(op = "<=", rhs = genStmt.lhs))
      // List(stmt.copy(op = "<="))

      case _ => List(stmt)
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
      case x @ VarLit(name) => x
      case x @ VarDymTyped(width, tp, name) =>
        tp match {
          case VarType.Input | VarType.Output =>
            x.copy(name = ioNameTransform(thisInstName, name))
          case _ => x
        }
      case x @ VarTyped(name, tp) =>
        tp match {
          case VarType.Input | VarType.Output =>
            x.copy(name = ioNameTransform(thisInstName, name))
          case _ => x
        }
    }
  }

  /** modify names for io: check if instantiated instance have same name, if so refer to
    * it by io.a, otherwise add inst name as prefix
    */
  def ioNameTransform(thisInstName: String, ioFullName: String) = {
    val instName :: name :: Nil = ioFullName.split('.').toList: @unchecked
    // rm inst name if refer in same mod
    val pfx = if thisInstName == instName then "io" else s"$instName.io"
    // dbg(pfx)
    s"$pfx.$name"
  }

  /** recursively apply expr to expr Transform like varNameTransform */
  def exprTransform(thisInstName: String, e: Expr[?]): Expr[?] = {
    e match {
      case v: Var[?] => varNameTransform(thisInstName, v)
      case x: BinOp[w] =>
        BinOp(
          exprTransform(thisInstName, x.a).asInstanceOf[Expr[Nothing]],
          exprTransform(thisInstName, x.b).asInstanceOf[Expr[Nothing]],
          x.nm
        )
      // case _: BoolEx[?] =>
      // case ExprAsBool(expr) =>
      // exprTransform(thisInstName, expr).asInstanceOf[Expr[Nothing]]
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
