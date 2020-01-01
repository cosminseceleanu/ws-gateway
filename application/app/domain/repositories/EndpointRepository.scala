package domain.repositories

import domain.model.Endpoint

import scala.concurrent.Future

trait EndpointRepository {
  def getAll(): Future[Seq[Endpoint]]
  def getById(id: String): Future[Option[Endpoint]]
  def create(endpoint: Endpoint): Future[Endpoint]
  def update(endpoint: Endpoint): Future[Endpoint]
  def delete(id: String): Future[Void]
}
