package common.rest.errors

import common.rest.JsonSupport
import javax.inject.{Inject, Provider, Singleton}
import play.api.{Configuration, Environment, OptionalSourceMapper, UsefulException}
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc.{RequestHeader, Result}
import play.api.mvc.Results._
import play.api.routing.Router
import play.mvc.Http.{HeaderNames, MimeTypes}

import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject()(
                              env: Environment,
                              config: Configuration,
                              sourceMapper: OptionalSourceMapper,
                              router: Provider[Router],
                              exceptionMappers: Set[ExceptionMapper]
                             ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with JsonSupport {
  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    exceptionMappers.find(_.supports(exception)) match {
      case Some(m) => Future.successful({
        val (status, resource) = m.mapToError(exception.asInstanceOf[m.E])
        new Status(status)(toJson(resource)).withHeaders(HeaderNames.CONTENT_TYPE -> MimeTypes.JSON)
      })
      case None => Future.successful(getDefaultErrorResponse(exception))
    }
  }

  private def getDefaultErrorResponse(exception: Throwable) = {
    val body = toJson(ErrorResource("InternalSeverError", exception.getMessage))
    InternalServerError(body).withHeaders(HeaderNames.CONTENT_TYPE -> MimeTypes.JSON)
  }
}
