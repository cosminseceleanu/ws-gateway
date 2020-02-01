package fixtures

import api.rest.resources.{HttpBackendResource, KafkaBackendResource}
import domain.model._

object BackendFixtures {

  def httpBackendResource: HttpBackendResource = HttpBackendResource("http://localhost", Map("X-Debug" -> "foo"), 1)
  def kafkaBackendResource: KafkaBackendResource = KafkaBackendResource("some.topic")

  def httpBackend: Backend[BackendSettings] = HttpBackend("http://localhost", HttpSettings(Map("X-Debug" -> "foo"), 1))
  def kafkaBackend: Backend[BackendSettings] = KafkaBackend("some.topic")
  def debugBackend: Backend[BackendSettings] = DebugBackend()
}
