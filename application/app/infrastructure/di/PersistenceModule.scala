package infrastructure.di

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import domain.repositories.EndpointRepository
import infrastructure.repositories.InMemoryEndpointRepository

class PersistenceModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[EndpointRepository])
      .annotatedWith(Names.named("endpointRepo"))
      .to(classOf[InMemoryEndpointRepository])
  }
}
