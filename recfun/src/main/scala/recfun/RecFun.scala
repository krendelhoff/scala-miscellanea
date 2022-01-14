package recfun

object RecFun extends RecFunInterface:

  def main(args: Array[String]): Unit =
    println("Pascal's Triangle")
    for row <- 0 to 10 do
      for col <- 0 to row do
        print(s"${pascal(col, row)} ")
      println()

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = (c,r) match {
    case (0,_) => 1
    case (c,r) if c == r => 1
    case (c,r) => pascal(c - 1, r - 1) + pascal(c, r - 1)
  }

  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean =
    (0, true) == chars.foldLeft((0, true))((acc, ch) => (ch, acc) match {
                              case ('(', (acc, flag)) => (acc + 1, flag)
                              case (')', (acc, _)) => (acc - 1, acc > 0)
                              case _   => acc
                            })

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int =
    (money, coins) match {
      case (money, _) if money < 0                => 0
      case (0, _)                                 => 1
      case (_, Nil)                               => 0
      case (money, coins)                         =>
        coins.scanRight(Nil:List[Int])(_::_)
             .tail
             .zip(coins)
             .map((tl, coin) =>
                  (1 to money/coin).map(n => countChange(money - n * coin, tl))
                                   .sum).sum
    }
