package domain.services

import domain.model.Endpoint
import domain.repositories.EndpointRepository
import javax.inject.{Inject, Named, Singleton}

import scala.concurrent.Future

@Singleton
class EndpointsProvider @Inject() (@Named("endpointRepo") endpointRepository: EndpointRepository) {
  def getAll(): Future[Seq[Endpoint]] = endpointRepository.getAll()
}
