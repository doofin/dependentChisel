/* import dependentChisel.syntax.*
import monadicTest.*
import monadicAST.*
import compilers.*
import dependentChisel.tests.doubleAdder.*

val compiler2 = new impureCompilerCls // compilerToStr
depChisel1.foldMap(compiler2)

// val compiler3 = new impureCompilerCls // compilerToStr
// adder1.foldMap(compiler3)
// compiler3.kvs(5)

val compiler4 = new impureCompilerCls // compilerToStr
doubleAdder.foldMap(compiler4)

val compiler5 = new impureCompilerCls // compilerToStr
adderDynamic.foldMap(compiler5)
 */
