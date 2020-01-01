package domain.services

import domain.repositories.EndpointRepository
import javax.inject.{Inject, Named, Singleton}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class EndpointDelete @Inject()(@Named("endpointRepo") endpointRepository: EndpointRepository, endpointsProvider: EndpointsProvider) {
  def delete(id: String): Future[Void] = endpointsProvider.get(id).flatMap(e => {endpointRepository.delete(e.id)})
}
