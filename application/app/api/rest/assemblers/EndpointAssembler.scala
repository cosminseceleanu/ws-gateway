package api.rest.assemblers

import api.rest.resources.{EndpointResource, FilterResource, RouteResource}
import common.rest.ResourceAssembler
import domain.model.{AuthenticationMode, Endpoint, Filter, Route}
import javax.inject.{Inject, Singleton}

@Singleton
class EndpointAssembler @Inject() (filterAssembler: FilterAssembler) extends ResourceAssembler[Endpoint, EndpointResource] {

  override def toModel(resource: EndpointResource): Endpoint = Endpoint(
    resource.id,
    resource.path,
    Set.empty[Filter],
    Set.empty[Route],
    AuthenticationMode.BASIC)

  override def toResource(model: Endpoint): EndpointResource = EndpointResource(
    model.id,
    model.path,
    Set.empty[FilterResource],
    Set.empty[RouteResource])
}
