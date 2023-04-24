package dependentChisel

import dependentChisel.typesAndSyntax.chiselModules.*

import dependentChisel.codegen.compiler.*
import scala.util.Try

object testUtils {

  /** pass firrtl compiler */
  def widthAndFirrtlOk[M <: UserModule](f: GlobalInfo => M) = {

    val verilog = for {
      firCirc <- Try {
        val (mod, depInfo: GlobalInfo) = makeModule(f)
        val fMod = chiselMod2firrtlCircuits(mod)
        // pp(fMod.modules map (_.modInfo))
        val firCirc = firrtlCircuits2str(fMod)
        println(firCirc)
        firCirc
      }

      vlg <- firrtlUtils.firrtl2verilog(firCirc)
    } yield vlg

    verilog.isSuccess
  }

}
