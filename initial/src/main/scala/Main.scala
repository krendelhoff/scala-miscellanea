/*
@main def hello: Unit =
  println("Hello world!")
  println(msg)
  println(findNb(4183059834009L))

def msg = "I was compiled by Scala 3. :)"

def findNb(m: Long): Int =
  val l:LazyList[(Int, Int)] =
    LazyList.from(1)
            .zip(LazyList.from(1)
                         .map(n => ((n + 1) * n / 2)^2))
  def foldF(x:(Int, Int), acc:Int):Int = x match
    case (i, y) => if y == m
                   then i
                   else if y > m
                        then -1
                   else acc
  l.foldRight(0)(foldF)

def gcd(a: Int, b: Int): Int = b match {
  case 0 => a
  case _ => gcd(b, a % b)
}

*/

object Main extends App {
  def main(args: Arra[String]) = println(5);
}
