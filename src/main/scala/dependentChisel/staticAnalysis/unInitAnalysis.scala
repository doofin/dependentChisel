package dependentChisel.staticAnalysis

import dependentChisel.codegen.sequentialCommands.AtomicCmds
import dependentChisel.staticAnalysis.MonotoneFramework.domainMapT

import dependentChisel.staticAnalysis.MonotoneFramework.MonoFrameworkT
import dependentChisel.codegen.sequentialCommands.NewInstance
import dependentChisel.codegen.sequentialCommands.WeakStmt
import dependentChisel.codegen.sequentialCommands.VarDecls
import dependentChisel.typesAndSyntax.typesAndOps.VarLit
import dependentChisel.typesAndSyntax.typesAndOps.BinOp
import dependentChisel.typesAndSyntax.typesAndOps.MulOp
import dependentChisel.typesAndSyntax.typesAndOps.AddOp
import dependentChisel.typesAndSyntax.typesAndOps.UniOp
import dependentChisel.typesAndSyntax.typesAndOps.VarDynamic
import dependentChisel.typesAndSyntax.typesAndOps.VarTyped
import dependentChisel.typesAndSyntax.typesAndOps.Lit
import dependentChisel.typesAndSyntax.typesAndOps.LitDym

/** check if vars have an value
  */
object unInitAnalysis {
  type mDomain = checkUnInitLattice.mDomain // which is Boolean
  type mStmt = AtomicCmds // statements

  val transferF: ((Int, mStmt, Int), domainMapT[mDomain]) => domainMapT[mDomain] = {
    case ((q0, cmd, q1), varmap) =>
      cmd match {
        case WeakStmt(lhs, op, rhs, prefix) =>
          rhs match
            case VarLit(name) => 
            case BinOp(a, b, nm) =>
            case MulOp(a, b, nm) =>
            case AddOp(a, b, nm) =>
            case UniOp(a, nm) =>
            case VarDynamic(width, tp, name) =>
            case VarTyped(name, tp) =>
            case Lit(i) =>
            case LitDym(i, width) =>
          
          // any assignment makes var initialized
          if op == ":=" then varmap.updated(lhs.getname, true) else varmap
        case _ => varmap
      }
  }

  object checkUnInitLattice extends semiLattice[Boolean] {

    override val smallerThan = {
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
      mBotMap: domainMapT[mDomain]
  ) = new MonoFrameworkT[mDomain, mStmt](
    transferF,
    mBotMap,
    checkUnInitLattice
  ) {}
}
