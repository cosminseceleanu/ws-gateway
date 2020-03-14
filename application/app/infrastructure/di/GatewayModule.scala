package infrastructure.di

import com.google.inject.AbstractModule
import gateway.authentication.{Authenticator, AuthenticationConnectionFilter, BasicAuthenticator, BearerAuthenticator, NoAuthChecker}
import gateway.backend.{BackendConnector, HttpConnector, LoggerConnector}
import gateway.connection.ConnectionFilter
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class GatewayModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    val connectorsBinder = ScalaMultibinder.newSetBinder[BackendConnector](binder)
    connectorsBinder.addBinding.to(classOf[LoggerConnector])
    connectorsBinder.addBinding.to(classOf[HttpConnector])

    val autenticatorsBinder = ScalaMultibinder.newSetBinder[Authenticator](binder)
    autenticatorsBinder.addBinding.to(classOf[NoAuthChecker])
    autenticatorsBinder.addBinding.to(classOf[BasicAuthenticator])
    autenticatorsBinder.addBinding.to(classOf[BearerAuthenticator])

    val connectionFiltersBinder = ScalaMultibinder.newSetBinder[ConnectionFilter](binder)
    connectionFiltersBinder.addBinding.to(classOf[AuthenticationConnectionFilter])
  }
}
