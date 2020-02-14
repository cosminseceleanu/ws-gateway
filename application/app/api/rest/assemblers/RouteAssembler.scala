package api.rest.assemblers

import api.rest.resources.{HttpBackendResource, KafkaBackendResource, RouteResource}
import common.rest.ResourceAssembler
import domain.model._
import javax.inject.{Inject, Singleton}

@Singleton
class RouteAssembler @Inject() (expressionAssembler: ExpressionAssembler) extends ResourceAssembler[Route, RouteResource] {

  override def toModel(resource: RouteResource): Route = {
    val httpBackends: Set[Backend[BackendSettings]] = resource.http
      .map(r => HttpBackend(r.destination, HttpSettings(r.additionalHeaders, r.timeout)))

    val kafkaBackends: Set[Backend[BackendSettings]] = resource.kafka
      .map(r => KafkaBackend(r.topic))

    val allBackends = httpBackends ++ kafkaBackends + DebugBackend()
    val expression = resource.expression.map(e => expressionAssembler.toModel(e))

    Route(RouteType.withName(resource.routeType), resource.name, allBackends, expression)
  }

  override def toResource(model: Route): RouteResource = {
    val http = model.backends
        .filter(_.backendType == BackendType.HTTP)
        .map(b => HttpBackendResource(
          b.destination,
          b.settings.asInstanceOf[HttpSettings].additionalHeaders,
          b.settings.asInstanceOf[HttpSettings].timeoutInMillis)
        )

    val kafka = model.backends
      .filter(_.backendType == BackendType.KAFKA)
      .map(b => KafkaBackendResource(b.destination))
    val expression = model.expression.map(e => expressionAssembler.toResource(e))

    RouteResource(model.routeType.toString, model.name, http, kafka, expression)
  }
}
