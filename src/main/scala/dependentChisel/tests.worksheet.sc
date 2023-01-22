import com.doofin.stdScalaJvm.*
import scala.compiletime.*
import dependentChisel.syntax.dslAST.wireTp
// import dslAST.wireTp
// import macroCall.*

println("Hello, world!")

val x = 1
x + x

dbg(x)

def test2() = {
  println("p test2")
  println("p test2")
}

test2()

constValueOpt[2]

val n2 = List(1, 2, 3).sum
val n3 = 3
inline val n4 = 3
// inline val n5 = n2 //err
constValueOpt[n2.type]
constValueOpt[n3.type]
constValueOpt[n4.type]

wireTp[1]().getVal
wireTp().getVal
wireTp[n4.type]().getVal
