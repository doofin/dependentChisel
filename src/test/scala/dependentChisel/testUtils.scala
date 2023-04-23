package dependentChisel

import dependentChisel.typesAndSyntax.chiselModules.*

import dependentChisel.codegen.compiler.*

object testUtils {

  def canPassFirrtl[M <: UserModule](f: GlobalInfo => M) = {
    val (mod, depInfo: GlobalInfo) = makeModule(f)
    val fMod = chiselMod2firrtlCircuits(mod)
    // pp(fMod.modules map (_.modInfo))
    val firCirc = firrtlCircuits2str(fMod)
    println(firCirc)

    val verilog = firrtlUtils.firrtl2verilog(firCirc)
    verilog.isSuccess
  }

}
