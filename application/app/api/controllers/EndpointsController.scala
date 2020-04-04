package api.controllers

import api.rest.assemblers.EndpointAssembler
import api.rest.resources.EndpointResource
import common.rest.errors.ErrorResource
import domain.exceptions.EndpointNotFoundException
import domain.services.{EndpointDelete, EndpointWriter, EndpointsProvider}
import javax.inject.Inject
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

class EndpointsController @Inject() (
                                      endpointsProvider: EndpointsProvider,
                                      endpointAssembler: EndpointAssembler,
                                      endpointWriter: EndpointWriter,
                                      endpointDelete: EndpointDelete,
                                      val controllerComponents: ControllerComponents
                                   ) extends BaseController {

  def getAll(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    endpointsProvider.getAll()
      .map(endpoints => endpointAssembler.toResourcesSeq(endpoints))
      .map(resources => Ok(Json.toJson(resources)))
  }

  def get(id: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    endpointsProvider.get(id)
      .map(endpoints => endpointAssembler.toResource(endpoints))
      .map(resources => Ok(Json.toJson(resources)))
  }

  def create(): Action[EndpointResource] = Action(validateJson[EndpointResource]).async { implicit request =>
    val endpointResource = request.body
    Future.successful(endpointAssembler.toModel(endpointResource))
      .flatMap(e => endpointWriter.create(e))
      .map(e => endpointAssembler.toResource(e))
      .map(r => Created(Json.toJson(r)))
  }

  def update(id: String): Action[EndpointResource] = Action(validateJson[EndpointResource]).async { implicit request =>
    val endpointResource = request.body
    Future.successful(endpointAssembler.toModel(endpointResource))
      .flatMap(e => endpointWriter.update(id, e))
      .map(e => endpointAssembler.toResource(e))
      .map(r => Ok(Json.toJson(r)))
  }

  private def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError(e).toString)) //@ToDo - refactor this
  )

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    endpointDelete.delete(id)
      .map(_ => NoContent)
  }
}
