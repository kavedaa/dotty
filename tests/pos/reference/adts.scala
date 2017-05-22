object t1 {

enum Option[+T] {
  case Some[T](x: T)
  case None
}

}

object t2 {

enum Option[+T] {
  case Some[T](x: T) extends Option[T]
  case None          extends Option[Nothing]
}


}

enum Color(val rgb: Int) {
  case Red   extends Color(0xFF0000)
  case Green extends Color(0x00FF00)
  case Blue  extends Color(0x0000FF)
  case Mix(mix: Int) extends Color(mix)
}

