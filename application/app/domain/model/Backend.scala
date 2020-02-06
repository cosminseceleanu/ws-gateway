package domain.model

import domain.model.BackendType.BackendType

sealed trait Backend[+T <: BackendSettings] {
  val backendType: BackendType
  val destination: String
  val settings: BackendSettings
}

case class DebugBackend() extends Backend[EmptySettings] {
  override val backendType: BackendType = BackendType.DEBUG
  override val destination: String = "debug"
  override val settings: BackendSettings = EmptySettings()
}

case class HttpBackend(destination: String, settings: BackendSettings) extends Backend[HttpSettings] {
  override val backendType: BackendType = BackendType.HTTP
}

case class KafkaBackend(topic: String) extends Backend[EmptySettings] {
  override val backendType: BackendType = BackendType.KAFKA
  override val destination: String = topic
  override val settings: BackendSettings = EmptySettings()
}

object Backend {
  def debug(): Backend[EmptySettings] = DebugBackend()
}
