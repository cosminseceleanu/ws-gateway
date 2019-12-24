package api.controllers

import api.rest.{EndpointResource, FilterResource, RouteResource}
import api.rest.assemblers.EndpointAssembler
import domain.services.EndpointsProvider
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

import scala.concurrent._
import ExecutionContext.Implicits.global



class EndpointsController @Inject() (
                                     endpointsProvider: EndpointsProvider,
                                     endpointAssembler: EndpointAssembler,
                                     val controllerComponents: ControllerComponents,
                                   ) extends BaseController {

  implicit val filterFormat = Json.format[FilterResource]
  implicit val routeFormat = Json.format[RouteResource]
  implicit val endpointsFormat = Json.format[EndpointResource]


  def getAll() = Action.async { implicit request: Request[AnyContent] =>
    endpointsProvider.getAll()
      .map(endpoints => endpointAssembler.toResource(endpoints))
      .map(resources => Ok(Json.toJson(resources)))
  }
}
