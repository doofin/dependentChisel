package dependentChisel

import com.doofin.stdScalaCross.*
import dependentChisel.typesAndSyntax.chiselModules.*

import dependentChisel.codegen.compiler.*
import scala.util.Try
import scala.util.Failure
import scala.util.Success

object testUtils {

  /** pass firrtl compiler */
  def checkWidthAndFirrtl[M <: UserModule](f: GlobalInfo => M) = {

    val verilog = for {
      firCirc <- Try {
        val (mod, depInfo: GlobalInfo) = makeModule(f)
        val fMod = chiselMod2firrtlCircuits(mod)
        // pp(fMod)
        // pp(fMod.modules map (_.modInfo))
        val firCirc = firrtlCircuits2str(fMod)
        // println(firCirc)
        firCirc
      }

      vlg <- firrtlUtils.firrtl2verilog(firCirc)
    } yield vlg
    val msg = verilog match
      case Failure(exception) => exception.getMessage()
      case Success(value)     => ""

    (msg, verilog.isSuccess)
  }

}
