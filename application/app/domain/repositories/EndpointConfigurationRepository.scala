package domain.repositories

import domain.model.{Endpoint, EndpointConfiguration}

import scala.concurrent.Future

trait EndpointConfigurationRepository {
  def getById(id: String): Future[EndpointConfiguration]
  def getLatest(): Future[EndpointConfiguration]
  def getByEndpoint(endpoint: Endpoint): Future[Set[EndpointConfiguration]]
  def add(endpoint: Endpoint, configuration: EndpointConfiguration): Future[EndpointConfiguration]
}
