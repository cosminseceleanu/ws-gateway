package domain.model

object AuthenticationMode extends Enumeration {
  type AuthenticationMode = Value
  val NONE, BASIC = Value
}
