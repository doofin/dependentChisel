package dependentChisel.staticAnalysis

import dependentChisel.staticAnalysis.MonotoneFramework.*
import dependentChisel.codegen.sequentialCommands.*
import dependentChisel.typesAndSyntax.typesAndOps.*

/** check if vars have an value
  */
object unInitAnalysis {
  type Domain = VarMap[Boolean]
  type Stmt = AtomicCmds // statements

  val transferF: ((Int, Stmt, Int), Domain) => Domain = { case ((q0, cmd, q1), varmap) =>
    cmd match {
      case WeakStmt(lhs, op, rhs, prefix) =>
        rhs match
          case VarLit(name)                =>
          case BinOp(a, b, nm)             =>
          case MulOp(a, b, nm)             =>
          case AddOp(a, b, nm)             =>
          case UniOp(a, nm)                =>
          case VarDynamic(width, tp, name) =>
          case VarTyped(name, tp)          =>
          case Lit(i)                      =>
          case LitDym(i, width)            =>

        // any assignment makes var initialized
        if op == ":=" then varmap.updated(lhs.getname, true) else varmap
      case _ => varmap
    }
  }

  /** lattice for uninitialized analysis is Var->Boolean where false means uninitialized
    *
    * we first define a simple boolean lattice and later lift it to Var->Boolean lattice
    */
  object unInitAnalysis extends semiLattice[Boolean] {

    override val leq = {
      case (_, true)      => true
      case (false, false) => true
      case _              => false
    }

    override val lub = {
      _ || _
    }

    override val bottom = false

  }

  def mMonoFramework(
      mBotMap: Domain
  ) = {
    // lift the boolean lattice to Var->Boolean lattice
    val lifted: semiLattice[VarMap[Boolean]] =
      unInitAnalysis.liftWithMap(mBotMap)

    MonoFrameworkT(
      transferF = transferF,
      baseLattice = lifted
    )
  }
}
