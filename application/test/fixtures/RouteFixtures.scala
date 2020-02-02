package fixtures

import api.rest.resources.{HttpBackendResource, RouteResource}
import domain.model._

object RouteFixtures {

  def defaultResource = RouteResource(RouteType.DEFAULT.toString, "Default Route")

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
}
