package domain.model

import domain.model.AuthenticationMode.AuthenticationMode
import org.joda.time.DateTime

case class EndpointConfiguration(
                                  id: String,
                                  filters: Set[Filter],
                                  routes: Set[Route],
                                  authenticationMode: AuthenticationMode,
                                  bufferSize: Int,
                                  backendParallelism: Int
                                ) extends Ordered[EndpointConfiguration] {

  val createdAt: DateTime = DateTime.now()

  override def compare(that: EndpointConfiguration): Int = createdAt.compareTo(that.createdAt)
}

object EndpointConfiguration {
  private val DEFAULT_BUFFER_SIZE = 50
  private val DEFAULT_BACKEND_PARALLELISM = 4

  def apply(filters: Set[Filter], routes: Set[Route]): EndpointConfiguration = {
    new EndpointConfiguration(None.orNull, filters, routes, AuthenticationMode.NONE, DEFAULT_BUFFER_SIZE, DEFAULT_BACKEND_PARALLELISM)
  }

  def apply(filters: Set[Filter], routes: Set[Route], authenticationMode: AuthenticationMode): EndpointConfiguration = {
    new EndpointConfiguration(None.orNull, filters, routes, authenticationMode, DEFAULT_BUFFER_SIZE, DEFAULT_BACKEND_PARALLELISM)
  }
}
