package infrastructure.di

import com.google.inject.AbstractModule
import gateway.authentication.{AuthenticationConnectionFilter, Authenticator, BasicAuthenticator, BearerAuthenticator, NoAuthChecker}
import gateway.backend.{BackendConnector, HttpConnector, LoggerConnector}

import infrastructure.AkkaClusterConfiguration

import gateway.connection.{ConnectionFilter, EndpointCustomFiltersFilter}

import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class GatewayModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind(classOf[AkkaClusterConfiguration]).asEagerSingleton()

    val connectorsBinder = ScalaMultibinder.newSetBinder[BackendConnector](binder)
    connectorsBinder.addBinding.to(classOf[LoggerConnector])
    connectorsBinder.addBinding.to(classOf[HttpConnector])

    val autenticatorsBinder = ScalaMultibinder.newSetBinder[Authenticator](binder)
    autenticatorsBinder.addBinding.to(classOf[NoAuthChecker])
    autenticatorsBinder.addBinding.to(classOf[BasicAuthenticator])
    autenticatorsBinder.addBinding.to(classOf[BearerAuthenticator])

    val connectionFiltersBinder = ScalaMultibinder.newSetBinder[ConnectionFilter](binder)
    connectionFiltersBinder.addBinding.to(classOf[AuthenticationConnectionFilter])
    connectionFiltersBinder.addBinding.to(classOf[EndpointCustomFiltersFilter])
  }
}
