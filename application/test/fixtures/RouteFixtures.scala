package fixtures

import api.rest.resources.{HttpBackendResource, RouteResource}
import domain.model._
import play.api.libs.json.JsObject

object RouteFixtures {

  def defaultResource: RouteResource = RouteResource(RouteType.DEFAULT.toString, "Default Route")

  def defaultResourceWithBackends: RouteResource = RouteResource(
    RouteType.DEFAULT.toString,
    "Default Route",
    Set(BackendFixtures.httpBackendResource),
    Set(BackendFixtures.kafkaBackendResource),
  )

  def connectResourceWithDefaultHttpBackend: RouteResource = RouteResource(
    RouteType.CONNECT.toString,
    "Connect",
    Set(BackendFixtures.httpBackendResource)
  )

  def connectResourceWithHttpBackend(httpBackend: HttpBackendResource): RouteResource = RouteResource(
    RouteType.CONNECT.toString,
    "Connect",
    Set(httpBackend)
  )

  def defaultResourceWithDefaultHttpBackend: RouteResource = RouteResource(
    RouteType.DEFAULT.toString,
    "Default Route",
    Set(BackendFixtures.httpBackendResource)
  )

  def defaultResourceWithHttpBackend(httpBackend: HttpBackendResource): RouteResource = RouteResource(
    RouteType.DEFAULT.toString,
    "Default Route",
    Set(httpBackend)
  )

  def disconnectResourceWithDefaultHttpBackend: RouteResource = RouteResource(
    RouteType.DISCONNECT.toString,
    "Disconnect",
    Set(BackendFixtures.httpBackendResource)
  )

  def disconnectResourceWithHttpBackend(httpBackend: HttpBackendResource): RouteResource = RouteResource(
    RouteType.DISCONNECT.toString,
    "Disconnect",
    Set(httpBackend)
  )

  def customResourceWithDefaultHttpBackend(name: String, jsObject: JsObject): RouteResource = RouteResource(
    RouteType.CUSTOM.toString,
    name,
    Set(BackendFixtures.httpBackendResource),
    Set.empty,
    Some(jsObject)
  )

  def customResourceWithHttpBackend(httpBackend: HttpBackendResource, name: String, jsObject: JsObject): RouteResource = RouteResource(
    RouteType.CUSTOM.toString,
    name,
    Set(httpBackend),
    Set.empty,
    Some(jsObject)
  )
}
