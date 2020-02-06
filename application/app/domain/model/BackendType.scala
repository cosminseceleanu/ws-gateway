package domain.model

object BackendType extends Enumeration {
  type BackendType = Value
  val HTTP, KAFKA, DEBUG = Value
}
