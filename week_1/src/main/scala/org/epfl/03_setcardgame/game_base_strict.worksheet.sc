case class Card(shape: Shape, number: Number, color: Color, shading: Shading)

enum Shape:
  case Diamond, Squiggle, Oval

enum Color:
  case Red, Green, Purple

enum Shading:
  case Open, Striped, Solid

enum Number:
  case One, Two, Three

// Let's create a deck of cards. Note that we are using Fully Qualified Names
// (FQN) to access the `enum` members. Later in the course, we will use one of
// the Scala 3 features that will allow us to avoid having to use FQNs
val deck = List(
  Card(Shape.Diamond,  Number.One, Color.Purple, Shading.Striped),
  Card(Shape.Squiggle, Number.Two, Color.Red,    Shading.Open),
  Card(Shape.Oval,     Number.Three, Color.Green,  Shading.Solid)
)

def isValidSet(card1: Card, card2: Card, card3: Card): Boolean =
  checkProperty(card1, card2, card3, _.shape)   &&
  checkProperty(card1, card2, card3, _.number)  &&
  checkProperty(card1, card2, card3, _.color)   &&
  checkProperty(card1, card2, card3, _.shading)

def checkProperty( card1: Card
                 , card2: Card
                 , card3: Card
                 , prop: Card => Shape | Color | Shading | Number
                 ): Boolean =
  def allSame =
    prop(card1) == prop(card2) && prop(card1) == prop(card3)
  def allDifferent =
    prop(card1) != prop(card2) &&
    prop(card1) != prop(card3) &&
    prop(card2) != prop(card3)
  allSame || allDifferent

isValidSet(
  Card(Shape.Diamond,  Number.One,   Color.Purple, Shading.Striped),
  Card(Shape.Squiggle, Number.Two,   Color.Purple, Shading.Open),
  Card(Shape.Oval,     Number.Three, Color.Purple, Shading.Solid)
)

isValidSet(
  Card(Shape.Diamond,  Number.Two,   Color.Purple, Shading.Striped),
  Card(Shape.Squiggle, Number.Two,   Color.Purple, Shading.Open),
  Card(Shape.Oval,     Number.Three, Color.Purple, Shading.Solid)
)
