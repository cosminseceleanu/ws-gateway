package infrastructure.repositories

import domain.model.{AuthenticationMode, Endpoint, Route, RouteType}
import domain.repositories.EndpointRepository

import scala.concurrent.Future

class InMemoryEndpointRepository extends EndpointRepository {

  private val defaultEndpoint = Endpoint(
    "id",
    "/demo",
    Set.empty,
    Set(Route(RouteType.CONNECT, "Connect")),
    AuthenticationMode.BASIC
    )

  private val endpoints: Map[String, Endpoint] = Map("1" -> defaultEndpoint)

  override def getAll(): Future[Seq[Endpoint]] = Future.successful(endpoints.values.toSeq)
}
