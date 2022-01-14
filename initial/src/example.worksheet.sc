sealed trait Lst[A]:
  def head: A
  def tail: Lst[A]
  def isEmpty: Boolean
  def nth(n: Int): A
  def foldl[B](f: (B, A) => B)(acc: B): B

case class Cons[A](head: A, tail: Lst[A]) extends Lst[A]:
  def isEmpty = false
  def foldl[B](f: (B, A) => B)(acc: B): B = tail.foldl(f)(f(acc, head))
  def nth(n: Int) = if n == 0 then head else tail.nth(n - 1)


class Nl[A] extends Lst[A]:
  override def toString = "Nil"
  def isEmpty = true
  def head = throw new NoSuchElementException("Nil.head")
  def tail = throw new NoSuchElementException("Nil.tail")
  def foldl[B](f: (B, A) => B)(acc: B): B = acc
  def nth(n: Int) = throw new IndexOutOfBoundsException(s"Nil.nth($n)")

Cons[Int](1, Cons[Int](2, Nl[Int]))

case class Num(value: Int)

given n: Num = Num(42)
def f(using x: Num): Int = x.value

println(f)
