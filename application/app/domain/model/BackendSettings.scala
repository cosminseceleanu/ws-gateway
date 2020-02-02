package domain.model

sealed trait BackendSettings {}

case class EmptySettings() extends BackendSettings

case class HttpSettings(additionalHeaders: Map[String, String], timeout: Int) extends BackendSettings
