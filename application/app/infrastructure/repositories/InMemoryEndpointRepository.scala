package infrastructure.repositories

import java.util.UUID

import domain.model.{AuthenticationMode, Endpoint, Route, RouteType}
import domain.repositories.EndpointRepository

import scala.collection.mutable
import scala.concurrent.Future

class InMemoryEndpointRepository extends EndpointRepository {

  private val defaultEndpoint = Endpoint(
    "id",
    "/demo",
    Set.empty,
    Set(Route(RouteType.CONNECT, "Connect")),
    AuthenticationMode.BASIC
    )

  private val endpoints: mutable.Map[String, Endpoint] = mutable.Map("1" -> defaultEndpoint)

  override def getAll(): Future[Seq[Endpoint]] = Future.successful(endpoints.values.toSeq)


  override def getById(id: String): Future[Option[Endpoint]] = Future.successful(endpoints.get(id))

  override def save(endpoint: Endpoint): Future[Endpoint] = {
    val toBeSaved = endpoint.copy(id = UUID.randomUUID().toString)
    endpoints. +(toBeSaved.id -> toBeSaved)
    Future.successful(toBeSaved)
  }
}
