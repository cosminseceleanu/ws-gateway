package domain.repositories

import domain.model.Endpoint

import scala.concurrent.Future

trait EndpointRepository {
  def getAll(): Future[Seq[Endpoint]]
  def getById(id: String): Future[Option[Endpoint]]
  def save(endpoint: Endpoint): Future[Endpoint]
}
