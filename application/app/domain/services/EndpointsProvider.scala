package domain.services

import domain.exceptions.EndpointNotFoundException
import domain.model.Endpoint
import domain.repositories.EndpointRepository
import javax.inject.{Inject, Named, Singleton}

import scala.concurrent._
import ExecutionContext.Implicits.global

@Singleton
class EndpointsProvider @Inject() (@Named("endpointRepo") endpointRepository: EndpointRepository) {
  def getAll(): Future[Seq[Endpoint]] = endpointRepository.getAll()

  def get(id: String): Future[Endpoint] = endpointRepository.getById(id).map({
    case Some(e) => e
    case None => throw EndpointNotFoundException(s"Endpoint not found")
  })
}
