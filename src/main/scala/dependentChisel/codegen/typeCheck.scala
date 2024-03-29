package dependentChisel.codegen
import scala.collection.mutable

// import com.doofin.stdScalaCross.*
import com.doofin.stdScalaJvm.*
import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.codegen.seqCommands.*

/** various checks like checking width */
object typeCheck {

  /** return if width check is ok,or the width of expr */
  private def getExprWidth(
      typeMap: mutable.Map[Expr[?] | Var[?], Int],
      expr: Expr[?]
  ): Int = {
    val tm = typeMap
    expr match {
      case BinOp(a, b, nm) =>
        val relOps = Seq("gt", "lt", "==")
        val isRelOp = relOps.contains(nm)

        val (i, j) = (getExprWidth(typeMap, a), getExprWidth(typeMap, b))
        val isWidthEqu = i == j
        assert(isWidthEqu, s"getExprWidth: Width mismatch $a $nm $b ")
        if isRelOp then 1 else i

      case UniOp(a, nm)     => tm(a)
      case Lit(i)           => i
      case LitDym(i, width) => width
      case x =>
        throwE(tm(x), "can't get type for " + x)
    }
  }

  def checkCmdWidth(
      typeMap: mutable.Map[Expr[?], Int],
      cmds: AtomicCmds
  ) = {
    cmds match {
      /* 1.add width field in FirStmt
        2. add width in lhs var and rhs expr
        3. use a map to store width of var and expr */
      case FirStmt(lhs, op, rhs, prefix) =>
        val lr = (getExprWidth(typeMap, lhs), getExprWidth(typeMap, rhs)) match {
          // only check if both result are numbers
          case lrWidth @ (i, j) =>
            val showPair = { (s: String, i: Int) => s"$s : ${i.toString().toRed()}" }

            val isWidthEqu = i == j
            val lhsGeqRhs = i >= j // firrtl allows width of lhs >= rhs in lhs:=rhs
            val isWidthOk = isWidthEqu // | lhsGeqRhs
            val msg =
              "[error]".toRed() +
                s" width mismatch in statement:\n${showPair(lhs.getname, i)}\n " +
                s"$op \n" +
                s"${showPair(rhs.toString(), j)} "

            // assert(isWidthOk, msg)
            if (!isWidthOk) {
              println(msg)
            }
            isWidthOk
        }
        lr

      case x => true
    }
  }

}
