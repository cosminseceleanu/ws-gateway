package gateway.backend

import domain.exceptions.BackendNotSupportedException
import domain.model.{Backend, BackendSettings, Route}
import javax.inject.{Inject, Singleton}

@Singleton
class ConnectorResolver @Inject() (private val connectors: Set[BackendConnector]) {

  def getConnectors(route: Route): Set[BackendConnector] = route.backends.map(getConnector)

  def getConnector(backend: Backend[BackendSettings]): BackendConnector = {
    connectors.find(c => c.supports(backend.backendType)) match {
      case Some(c) => c
      case None => throw BackendNotSupportedException(s"Backend of type ${backend.backendType} is not supported")
    }
  }
}
