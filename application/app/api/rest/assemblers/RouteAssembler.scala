package api.rest.assemblers

import api.rest.resources.RouteResource
import common.rest.ResourceAssembler
import domain.model.{Route, RouteType}
import javax.inject.Singleton

@Singleton
class RouteAssembler extends ResourceAssembler[Route, RouteResource] {

  override def toModel(resource: RouteResource): Route = Route(RouteType.withName(resource.routeType), resource.name)

  override def toResource(model: Route): RouteResource = RouteResource(model.routeType.toString, model.name)
}
