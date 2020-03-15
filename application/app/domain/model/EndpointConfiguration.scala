package domain.model

import java.util

import common.validation.Validatable
import common.validation.constraints.TraversableSize
import domain.model.RouteType.RouteType
import domain.validation.constraints.ValidRouteConfiguration
import javax.validation.Valid
import javax.validation.constraints.{Max, Min, NotNull}
import org.joda.time.DateTime

import scala.annotation.meta.field
import scala.collection.JavaConverters._

@ValidRouteConfiguration
case class EndpointConfiguration(
                                  id: String,
                                  @(NotNull @field) @(TraversableSize @field)(max = 255) filters: Set[Filter],
                                  @(NotNull @field) @(TraversableSize @field)(max = 255) routes: Set[Route],
                                  @(NotNull @field) @(Valid @field) authentication: Authentication,
                                  @(Min @field)(10) @(Max @field)(10000) @(NotNull @field) bufferSize: Int,
                                  @(Min @field)(1) @(Max @field)(32) @(NotNull @field) backendParallelism: Int,
                                  @(NotNull @field) createdAt: DateTime
                                ) extends Ordered[EndpointConfiguration] with Validatable {

  override def compare(that: EndpointConfiguration): Int = createdAt.compareTo(that.createdAt)

  def getRoutes(routeType: RouteType): Set[Route] = routes.filter(_.routeType == routeType)

  @Valid
  private val filterSet: util.Collection[Filter] = filters.asJavaCollection

  @Valid
  private val routeSet: util.Collection[Route] = routes.asJavaCollection
}

object EndpointConfiguration {
  val DEFAULT_BUFFER_SIZE = 50
  val DEFAULT_BACKEND_PARALLELISM = 4

  def apply(filters: Set[Filter], routes: Set[Route]): EndpointConfiguration = {
    new EndpointConfiguration(
      None.orNull, filters, routes,
      Authentication.None(), DEFAULT_BUFFER_SIZE, DEFAULT_BACKEND_PARALLELISM,
      DateTime.now()
    )
  }

  def apply(filters: Set[Filter], routes: Set[Route], authentication: Authentication): EndpointConfiguration = {
    new EndpointConfiguration(
      None.orNull, filters, routes,
      authentication, DEFAULT_BUFFER_SIZE, DEFAULT_BACKEND_PARALLELISM,
      DateTime.now()
    )
  }

  def apply(filters: Set[Filter], routes: Set[Route],
            authentication: Authentication, bufferSize: Int, backendParallelism: Int): EndpointConfiguration = {
    new EndpointConfiguration(None.orNull, filters, routes, authentication, bufferSize, backendParallelism, DateTime.now())
  }

  def apply(id: String, filters: Set[Filter], routes: Set[Route],
            authentication: Authentication, bufferSize: Int,
            backendParallelism: Int, createdAt: DateTime): EndpointConfiguration = {
    new EndpointConfiguration(id, filters, routes, authentication, bufferSize, backendParallelism, createdAt)
  }
}
