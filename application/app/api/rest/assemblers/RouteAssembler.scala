package api.rest.assemblers

import api.rest.resources.{HttpBackendResource, KafkaBackendResource, RouteResource}
import common.rest.ResourceAssembler
import domain.model._
import javax.inject.Singleton

@Singleton
class RouteAssembler extends ResourceAssembler[Route, RouteResource] {

  override def toModel(resource: RouteResource): Route = {
    val httpBackends: Set[Backend[BackendSettings]] = resource.http
      .map(r => HttpBackend(r.destination, HttpSettings(r.additionalHeaders, r.timeout)))

    val kafkaBackends: Set[Backend[BackendSettings]] = resource.kafka
      .map(r => KafkaBackend(r.topic))

     val allBackends = httpBackends ++ kafkaBackends + DebugBackend()

    Route(RouteType.withName(resource.routeType), resource.name, allBackends)
  }

  override def toResource(model: Route): RouteResource = {
    val http = model.backends
        .filter(_.backendType == BackendType.HTTP)
        .map(b => HttpBackendResource(
          b.destination,
          b.settings.asInstanceOf[HttpSettings].additionalHeaders,
          b.settings.asInstanceOf[HttpSettings].timeout)
        )

    val kafka = model.backends
      .filter(_.backendType == BackendType.KAFKA)
      .map(b => KafkaBackendResource(b.destination))


    RouteResource(model.routeType.toString, model.name, http, kafka)
  }
}
