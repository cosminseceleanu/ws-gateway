package api.rest.resources

import play.api.libs.json.{Json, OFormat}

case class RouteResource(routeType: String, name: String)

object RouteResource {
  implicit val format: OFormat[RouteResource] = Json.format[RouteResource]
}
