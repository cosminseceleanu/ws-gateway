package domain.services

import domain.exceptions.EndpointNotFoundException
import domain.model.Endpoint
import domain.repositories.EndpointRepository
import javax.inject.{Inject, Named, Singleton}

import scala.concurrent._
import ExecutionContext.Implicits.global

@Singleton
class EndpointsProvider @Inject() (@Named("endpointRepo") endpointRepository: EndpointRepository) {
  def getFirstMatch(path: String): Future[Option[Endpoint]] = getAll()
    .map(endpoints => endpoints.find(e => e.matchesPath(path)))

  def getAll(): Future[Seq[Endpoint]] = endpointRepository.getAll()

  def get(id: String): Future[Endpoint] = endpointRepository.getById(id)
    .map(getEndpointOrThrow)

  private def getEndpointOrThrow(maybeEndpoint: Option[Endpoint]): Endpoint = maybeEndpoint match {
    case Some(e) => e
    case None => throw EndpointNotFoundException(s"Endpoint not found")
  }
}
