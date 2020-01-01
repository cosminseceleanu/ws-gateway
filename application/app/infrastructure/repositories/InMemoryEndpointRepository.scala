package infrastructure.repositories

import java.util.UUID

import domain.model.{AuthenticationMode, Endpoint, Route, RouteType}
import domain.repositories.EndpointRepository
import javax.inject.Singleton

import scala.collection.mutable
import scala.concurrent.Future

@Singleton
class InMemoryEndpointRepository extends EndpointRepository {

  private val defaultEndpoint = Endpoint(
    "id",
    "/demo",
    Set.empty,
    Set(Route(RouteType.CONNECT, "Connect")),
    AuthenticationMode.BASIC
    )

  private val endpoints: mutable.Map[String, Endpoint] = mutable.Map("id" -> defaultEndpoint)

  override def getAll(): Future[Seq[Endpoint]] = endpoints.synchronized {
    Future.successful(endpoints.values.toSeq)
  }

  override def getById(id: String): Future[Option[Endpoint]] = endpoints.synchronized {
    Future.successful(endpoints.get(id))
  }

  override def create(endpoint: Endpoint): Future[Endpoint] = endpoints.synchronized {
    val toBeSaved = endpoint.copy(id = UUID.randomUUID().toString)
    endpoints. += (toBeSaved.id -> toBeSaved)
    Future.successful(toBeSaved)
  }

  override def update(endpoint: Endpoint): Future[Endpoint] = {
    endpoints. += (endpoint.id -> endpoint)
    Future.successful(endpoint)
  }

  override def delete(id: String): Future[Void] = endpoints.synchronized {
    endpoints. -=(id)
    Future.successful(None.orNull)
  }
}
