package api.rest.assemblers

import api.rest.resources.EndpointResource
import common.rest.ResourceAssembler
import domain.model.{Endpoint, EndpointConfiguration}
import javax.inject.{Inject, Singleton}

@Singleton
class EndpointAssembler @Inject() (
                                    filterAssembler: FilterAssembler,
                                    routeAssembler: RouteAssembler,
                                    authenticationAssembler: AuthenticationAssembler
                                  ) extends ResourceAssembler[Endpoint, EndpointResource] {

  override def toModel(resource: EndpointResource): Endpoint = Endpoint(
    resource.id.orNull,
    resource.path,
    EndpointConfiguration(
      filterAssembler.toModel(resource.filters),
      routeAssembler.toModelsSet(resource.routes),
      authenticationAssembler.toModel(resource.authentication),
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
    authenticationAssembler.toResource(model.authentication)
    )
}
