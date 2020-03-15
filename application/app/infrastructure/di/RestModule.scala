package infrastructure.di

import api.exceptionmappers._
import com.google.inject.AbstractModule
import common.rest.errors.ExceptionMapper
import common.rest.errors.mappers.ConstraintViolationExceptionMapper
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class RestModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    val mappersBinder = ScalaMultibinder.newSetBinder[ExceptionMapper](binder)

    mappersBinder.addBinding.to(classOf[ConstraintViolationExceptionMapper])
    mappersBinder.addBinding.to(classOf[BackendNotSupportedExceptionMapper])
    mappersBinder.addBinding.to(classOf[EndpointNotFoundExceptionMapper])
    mappersBinder.addBinding.to(classOf[ExpressionNotSupportedExceptionMapper])
    mappersBinder.addBinding.to(classOf[IncorrectBooleanExpressionExceptionMapper])
    mappersBinder.addBinding.to(classOf[IncorrectExpressionExceptionMapper])
    mappersBinder.addBinding.to(classOf[AuthenticationNotSupportedExceptionMapper])
  }
}
