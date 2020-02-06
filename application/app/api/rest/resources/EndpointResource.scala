package api.rest.resources

import play.api.libs.json.{Json, OFormat}

case class EndpointResource(
                       id: Option[String],
                       path: String,
                       filters: FilterResource,
                       routes: Set[RouteResource],
                       authenticationMode: String) {

  def httpBackends: List[HttpBackendResource] = routes.toList.flatMap(_.http)
}

object EndpointResource {
  implicit val format: OFormat[EndpointResource] = Json.format[EndpointResource]
}
