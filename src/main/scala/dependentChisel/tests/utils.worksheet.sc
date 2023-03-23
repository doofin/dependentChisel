import java.util.concurrent.atomic.DoubleAdder
import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.*

import com.doofin.stdScalaCross.*
import com.doofin.stdScala.mainRunnable

import dependentChisel.typesAndSyntax.basicTypes.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.control.*

import dependentChisel.tests.adder.*

val (mod, global) = makeModule { implicit p =>
  new AdderDouble
}
pp(mod.modLocalInfo)
pp(global)

val y = 1
dbg(y)
