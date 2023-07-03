package dependentChisel.misc

import java.util.Random

object scala3features {
  import scala.quoted.* // imports Quotes, Expr

//type class: https://docs.scala-lang.org/scala3/reference/contextual/type-classes.html#
  trait Functor[F[_]] {
    extension [A](x: F[A]) {
      def map[B](f: A => B): F[B]
    }
  }

  trait Monad[F[_]] extends Functor[F]:

    /** The unit value for a monad */
    def pure[A](x: A): F[A]

    extension [A](x: F[A]) {

      /** The fundamental composition operation */
      def flatMap[B](f: A => F[B]): F[B]

      /** The `map` operation can now be defined in terms of `flatMap` */
      // def map[B](f: A => B) = x.flatMap(f.andThen(pure))
    }
  end Monad

  given Functor[List] with {
    extension [A](x: List[A]) { override def map[B](f: A => B): List[B] = x.map(f) }
  }
  given listMonad: Monad[List] with {

    extension [A](x: List[A]) override def map[B](f: A => B): List[B] = ???

    def pure[A](x: A): List[A] =
      List(x)
    extension [A](xs: List[A])
      def flatMap[B](f: A => List[B]): List[B] =
        xs.flatMap(f) // rely on the existing `flatMap` method of `List`
  }

  given exprMonad: Monad[Expr] with {

    override def pure[A](x: A): Expr[A] = ???

    extension [A](x: Expr[A]) {
      override def map[B](f: A => B): Expr[B] = ???
      override def flatMap[B](f: A => Expr[B]): Expr[B] = ???
    }

  }

  /** Enum Types: http://dotty.epfl.ch/docs/reference/enums/adts.html
    */
  enum ListEnum[+A] {
    case Cons(h: A, t: ListEnum[A])
    case Empty
  }

  enum Planet(mass: Double, radius: Double) {
    private final val G = 6.67300e-11
    def surfaceGravity = G * mass / (radius * radius)
    def surfaceWeight(otherMass: Double) = otherMass * surfaceGravity

    case Earth extends Planet(5.976e+24, 6.37814e6)
  }

  enum E1 {
    case c1(a: Int)
    case c2
  }
//poly function : https://docs.scala-lang.org/scala3/reference/new-types/polymorphic-function-types.html
  val bar: [A] => List[A] => List[A] = [A] => (xs: List[A]) => xs

  def test1 = {

    if true then "true" else "false"

    val tup1 = (1, 2, "str")
    tup1.splitAt(1)
    tup1.size
    val tupTail = tup1.tail
    val toList = (1, 'a', 2).toList
    val natTrans = (1, 'a').map[[X] =>> Option[X]]([T] => (t: T) => Some(t))

    val rand = new Random()

    while true do
      rand.nextFloat()
      rand.nextBoolean()
  }
// https://www.baeldung.com/scala/structural-types
  import reflect.Selectable.reflectiveSelectable
  type Flyer = {
    def fly(): Unit
    val name: String
  }
  def callFly(thing: Flyer): Unit = thing.fly()

// https://docs.scala-lang.org/scala3/book/types-structural.html
  class RecordT(elems: (String, Any)*) extends Selectable:
    private val fields = elems.toMap
    def selectDynamic(name: String): Any = fields(name)

  type Person = RecordT {
    val name: String
    val age: Int
  }

  val person = RecordT(
    "name" -> "Emma",
    "age" -> 42
  ).asInstanceOf[Person]

  person.age

  /*
  Context Functions
  https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html
  https://www.scala-lang.org/blog/2016/12/07/implicit-function-types.html
   */

  def imp(f: Int ?=> Unit) = {}
  val f: Int ?=> Unit = x ?=> ()
  val lf: Seq[Int ?=> Unit] = Seq(f)

  case class I(x: Int)

  def f(g: I ?=> String) = {
    given i: I = I(1)
    g
  }

  transparent inline def default(inline name: String) = {
    inline if name == "Int" then 0
    else ""
  }

  default("Int")
  val n0: Int = default("Int")
  val s0: String = default("String")

  /* inline def doSomething(inline mode: Boolean): Unit = {
    if mode then 1
    else if !mode then 1
    else println("Mode must be a known value but got: " + codeOf(mode))
  }
  val bool: Boolean = true
  doSomething(bool) */

}
