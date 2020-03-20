package infrastructure.di

import com.google.inject.AbstractModule
import gateway.backend.{BackendConnector, HttpConnector, LoggerConnector}
import infrastructure.AkkaClusterConfiguration
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class GatewayModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind(classOf[AkkaClusterConfiguration]).asEagerSingleton()

    val connectorsBinder = ScalaMultibinder.newSetBinder[BackendConnector](binder)
    connectorsBinder.addBinding.to(classOf[LoggerConnector])
    connectorsBinder.addBinding.to(classOf[HttpConnector])
  }
}
