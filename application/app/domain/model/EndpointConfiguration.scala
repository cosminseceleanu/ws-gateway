package domain.model

import java.util

import common.validation.Validatable
import domain.model.AuthenticationMode.AuthenticationMode
import javax.validation.Valid
import javax.validation.constraints.{Max, Min, NotNull}
import org.joda.time.DateTime

import scala.annotation.meta.field
import scala.collection.JavaConverters._

//@ToDo add validation to ensure route type
case class EndpointConfiguration(
                                  id: String,
                                  @(NotNull @field) filters: Set[Filter],
                                  @(NotNull @field) routes: Set[Route],
                                  @(NotNull @field) authenticationMode: AuthenticationMode,
                                  @(Min @field)(10) @(Max @field)(10000) @(NotNull @field) bufferSize: Int,
                                  @(Min @field)(1) @(Max @field)(32) @(NotNull @field) backendParallelism: Int,
                                  @(NotNull @field) createdAt: DateTime
                                ) extends Ordered[EndpointConfiguration] with Validatable {

  override def compare(that: EndpointConfiguration): Int = createdAt.compareTo(that.createdAt)

  @Valid
  private val filterSet: util.Collection[Filter] = filters.asJavaCollection

  @Valid
  private val routeSet: util.Collection[Route] = routes.asJavaCollection
}

object EndpointConfiguration {
  private val DEFAULT_BUFFER_SIZE = 50
  private val DEFAULT_BACKEND_PARALLELISM = 4

  def apply(filters: Set[Filter], routes: Set[Route]): EndpointConfiguration = {
    new EndpointConfiguration(
      None.orNull, filters, routes,
      AuthenticationMode.NONE, DEFAULT_BUFFER_SIZE, DEFAULT_BACKEND_PARALLELISM,
      DateTime.now()
    )
  }

  def apply(filters: Set[Filter], routes: Set[Route], authenticationMode: AuthenticationMode): EndpointConfiguration = {
    new EndpointConfiguration(
      None.orNull, filters, routes,
      authenticationMode, DEFAULT_BUFFER_SIZE, DEFAULT_BACKEND_PARALLELISM,
      DateTime.now()
    )
  }

  def apply(id: String, filters: Set[Filter], routes: Set[Route],
            authenticationMode: AuthenticationMode, bufferSize: Int,
            backendParallelism: Int, createdAt: DateTime): EndpointConfiguration = {
    new EndpointConfiguration(id, filters, routes, authenticationMode, bufferSize, backendParallelism, createdAt)
  }

}
