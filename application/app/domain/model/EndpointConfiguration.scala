package domain.model

import domain.model.AuthenticationMode.AuthenticationMode
import org.joda.time.DateTime

case class EndpointConfiguration(
                                  id: String,
                                  filters: Set[Filter], routes: Set[Route],
                                  authenticationMode: AuthenticationMode
                                ) extends Ordered[EndpointConfiguration] {

  val createdAt: DateTime = DateTime.now()

  override def compare(that: EndpointConfiguration): Int = createdAt.compareTo(that.createdAt)
}

object EndpointConfiguration {
  def apply(filters: Set[Filter], routes: Set[Route]): EndpointConfiguration = {
    new EndpointConfiguration(null, filters, routes, AuthenticationMode.NONE)
  }

  def apply(filters: Set[Filter], routes: Set[Route], authenticationMode: AuthenticationMode): EndpointConfiguration = {
    new EndpointConfiguration(null, filters, routes, authenticationMode)
  }
}
