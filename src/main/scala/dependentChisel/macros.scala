package dependentChisel

import dependentChisel.scala3features
/*  Quoting and splicing are combined with inline to cause this macro implementation
to do compile-time metaprogramming.
 */
import pprint.*
import scala.quoted.* // imports Quotes, Expr
import com.doofin.stdScalaCross.*

object macros {
  type PlusTwo[T <: Int] = scala.compiletime.ops.int.+[2, T]

  inline def repeat(s: String, count: Int): String =
    if count == 0 then ""
    else s + repeat(s, count - 1)

  inline def doSomething(inline mode: Boolean): String =
    if mode then "" else ""

  def run = {
    val a: PlusTwo[4] = 6
    doSomething(true)
    repeat("hello", 3) // Okay

    var n = 3
  }

  class c1(p1: Int) {
    val n1 = 1
  }
  // repeat("hello", n) // ERROR!

  /* Quotation and Splicing
  Quotation converts a code expression into a typed AST: T=> Expr[T]
  '{…}

  Splicing converts ast Expr[T] to an expression of type T
  ${…}
   */

  /* prints the expression of the provided argument at compile-time
  this executes as normal code
   */
  def inspectSimple1(x: Expr[Any])(using Quotes): Expr[Any] = {
    import quotes.reflect.*
    println(x.show) // ok
    println("inspectSimple1")
    // x.asTer
    val tm = x.asTerm
    pp(tm)
    tm.show(using Printer.TreeStructure) // not printed
    println("inspectSimple1 end")
    x
  }

  inline def inspect(inline x: Any): Any = ${ inspectSimple1('x) }

  inline def getTypeTerm[T](inline x: T) = ${ getTypeTermImp('x) } //

  inline def getTypeInfo[T] = ${ getTypeImp } //

  inline def toFir[T](inline x: T) = ${ toFirImp('x) } //

  def toFirImp[T: Type](x: Expr[T])(using Quotes) = encloseDebug("toFir:") {

    import quotes.reflect.*
    val tpe: TypeRepr = TypeRepr.of[T]
    val tpet: Type[T] = Type.of[T]
    println(s"type: ${tpe.show}")

    val typeSybs: Symbol = tpe.typeSymbol

    val valsList = getVals(tpe)
      .filter(_._1.isValDef)
      .map(x =>
        val nm = x._1.toString().replace("val ", "")
        s"$nm:${x._2.replace("UIntDep", "UInt")}"
      )
    // ppc(valsList)

    val resu = " output io : { " + valsList.mkString(",") + "}"
    ppc("generated firrtl bundle :") // output io : { flip a : UInt<32>, flip b : UInt<32>, y : UInt<32>}
    println(resu)
    // assert(1 == 1)
    Expr(resu)
  }

  inline def bundleEqu[T, r](inline x: T, inline y: r) = ${ bundleEquImp('x, 'y) } //

  def exprMonadTest(x: Expr[Int])(using Quotes) = {
    import scala3features.exprMonad._
    x.flatMap(y => Expr(y))
    for {
      i <- x
      j <- x
    } yield i + j
  }

  def getTypeImp[T: Type](using Quotes) = encloseDebug("getType") {
    import quotes.reflect.*
    val tpe: TypeRepr = TypeRepr.of[T]
    println(s"type:${tpe.show}")
    val typeSybs: Symbol = tpe.typeSymbol
    pp(typeSybs)
    val decl: List[Symbol] = typeSybs.declarations
    def type2expr(tpR: TypeRepr) = {
      tpR.asType match
        case '[t] => '{ val x: t = ??? }
    }
    ppc(getVals(tpe).filter(_._1.isValDef))
    Expr(decl.toString())
  }
  def getVals2(using q: Quotes)(rootType: q.reflect.TypeRepr) = {}

  def getVals(using Quotes)(rootType: quotes.reflect.TypeRepr) = {
    import quotes.reflect.*
    val typeSybs: Symbol = rootType.typeSymbol
    val decl: List[Symbol] = typeSybs.declarations
    decl.map { x =>
      val tpR: TypeRepr = rootType.memberType(x)
      val tp = tpR.show.split('.').last
      (x, tp) // get type of members in the class as TypeRepr
    }
  }

  def bundleEquImp[T1: Type, T2: Type](x: Expr[T1], y: Expr[T2])(using Quotes) =
    encloseDebug("bundleEquImp") {
      val x1 = toFirImp[T1](x)
      val y1 = toFirImp[T2](y)
      assert(x1.value.get == y1.value.get, "bundle must be same shape")
      Expr("")
    }

// https://docs.scala-lang.org/scala3/guides/macros/reflection.html#to-symbol-and-back
  def getTypeTermImp[T: Type](x: Expr[T])(using Quotes) = encloseDebug("getTypeTermImp:") {

    import quotes.reflect.*
    val tpe: TypeRepr = TypeRepr.of[T]
    val tpet: Type[T] = Type.of[T]
    println(s"type:${tpe.show}")
    /*  Type[T] is a wrapper over TypeRepr, with T being the statically-known type.
     To get a TypeRepr, we use TypeRepr.of[T], which expects a given Type[T] */

    // tpe.asType match
    //   case '[t] => '{ val x: t = 1.asInstanceOf[t] }

    val typeSybs: Symbol = tpe.typeSymbol

    println("fullName:" + typeSybs.fullName)
    val typeSybPos = typeSybs.pos.get
    val codeLine = typeSybPos.startLine.toString
    // rust's dbg . https://blog.softwaremill.com/starting-with-scala-3-macros-a-short-tutorial-88e9d2b2584c
    // println(s"code position: ${pos.sourceFile.name} ln:" + codeLine)
    val decl: List[Symbol] = typeSybs.declarations
    // typeSybs
    val declTp = typeSybs.declaredTypes
    val declTp2 = typeSybs.typeRef
    val decl1 = typeSybs.declaredMethods
    // decl.foreach(s => println(s.show(using Printer.TreeStructure)))
    println("typeSybs.declarations")
    ppc(getVals(tpe).filter(_._1.isValDef))

    // pp(decl)
    // pp(declTp)
    // pp(declTp2)
    // https://docs.scala-lang.org/scala3/guides/macros/best-practices.html

    /* AppliedType(
      tycon = TypeRef(
        prefix = ThisType(
          tref = TypeRef(
            prefix = ThisType(tref = TypeRef(prefix = NoPrefix, myDesignator = module class syntax)),
            myDesignator = module class bundles$
          )
        ),
        myDesignator = class UintDep
      ),
      args = List(ConstantType(value = ( = 8)))
     */

    /*
    signature : method equals, Signature(paramsSig = List(java.lang.Object), resSig = scala.Boolean))
     x.flags.show: method toString, "Flags.Method | Flags.Override | Flags.Synthetic") */
    val retStr = typeSybs.toString() + "," + decl
    val sybsE = Expr(retStr)
    // println("sybsE:" + sybsE)
    val resu: Expr[(T, String)] = '{ ($x, $sybsE) }
    // println("quoted:" + resu.show)
    resu
  }

  def code(x: Expr[Int])(using Quotes) = {
    println(x.show)
    List(1).map(x => x)

    '{ $x + 1 }
  }

// https://github.com/sirthias/macrolizer
  /*
     macrolizer.show {
    val x = 1
  }
   */
  // '{ val y = 3; ${ code('y) } } // y+1

//check if n is a quoted constant
  def powCodeFor(x: Expr[Int], n: Expr[Int])(using Quotes) = {
    n match
      case Expr(m) => // powCode(x, m) // it is a constant: unlift code n='{m} into number m
      case _       => // '{ power($x, $n) } // not known: call power at runtime
  }

  import scala.compiletime.erasedValue

  inline def f[T](l: List[T]) = inline erasedValue[T] match {
    case _: Int    => println("int list")
    case _: String => println("string list")
    case _         => println("general list")
  }
}
