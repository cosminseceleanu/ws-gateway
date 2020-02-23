package api.rest.assemblers

import api.rest.resources.EndpointResource
import common.rest.ResourceAssembler
import domain.model.{AuthenticationMode, Endpoint, EndpointConfiguration}
import javax.inject.{Inject, Singleton}

@Singleton
class EndpointAssembler @Inject() (filterAssembler: FilterAssembler, routeAssembler: RouteAssembler) extends ResourceAssembler[Endpoint, EndpointResource] {

  override def toModel(resource: EndpointResource): Endpoint = Endpoint(
    resource.id.orNull,
    resource.path,
    EndpointConfiguration(
      filterAssembler.toModel(resource.filters),
      routeAssembler.toModelsSet(resource.routes),
      AuthenticationMode.withName(resource.authenticationMode),
      resource.bufferSize.getOrElse(EndpointConfiguration.DEFAULT_BUFFER_SIZE),
      resource.backendParallelism.getOrElse(EndpointConfiguration.DEFAULT_BACKEND_PARALLELISM)
    ))

  override def toResource(model: Endpoint): EndpointResource = EndpointResource(
    Some(model.id),
    model.path,
    Some(model.backendParallelism),
    Some(model.bufferSize),
    filterAssembler.toResource(model.filters),
    routeAssembler.toResourcesSet(model.routes),
    model.authenticationMode.toString)
}
