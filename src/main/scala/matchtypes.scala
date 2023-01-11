// https://github.com/deanwampler/programming-scala-book-code-examples/blob/66afb28cdeb12734c999eaeb8ecc84535528aa88/src/script/scala/progscala3/typesystem/matchtypes/DepTypedMethods.scala

import compiletime.asMatchable // <3>

object matchtypes {
  /*
type family like https://docs.idris-lang.org/en/latest/tutorial/typesfuns.html#dependent-types

isSingleton : Bool -> Type
isSingleton True = Nat
isSingleton False = List Nat
   */
  type ElemR[X] = X match // "R" for "recursive"
    case String      => Char
    case Array[t]    => ElemR[t] // <1>
    case Iterable[t] => ElemR[t] // <2>
    case Option[t]   => ElemR[t]
    case AnyVal      => X

  /*
mkSingle : (x : Bool) -> isSingleton x
mkSingle True = 0
mkSingle False = []
   */
  def first[X](x: X): ElemR[X] =
    x.asMatchable match // unsafe,may delay to run time if x is not available at compile time
      case s: String      => s.charAt(0)
      case a: Array[t]    => first(a(0))
      case i: Iterable[t] => first(i.head)
      case o: Option[t]   => first(o.get)
      case x: AnyVal      => x
// end::first[]

// tag::example[]
  case class C(name: String)
  object O
  def run = {
    first("one")
    first(Array(2.2, 3.3))
    first(Seq("4", "five"))
    // first(6)
    // first(true)
    // first(O)// err
    first(C("Dean"))
  }
}