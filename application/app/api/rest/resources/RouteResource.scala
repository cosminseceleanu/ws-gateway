package api.rest.resources

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class RouteResource(routeType: String, name: String,
                         http: Set[HttpBackendResource], kafka: Set[KafkaBackendResource]
                        )

object RouteResource {

  implicit val routeWrites: Writes[RouteResource] = (
    (JsPath \ "type").write[String] and
      (JsPath \ "name").write[String] and
      (JsPath \ "http").write[Set[HttpBackendResource]] and
      (JsPath \ "kafka").write[Set[KafkaBackendResource]]
    ) (unlift(RouteResource.unapply))

  implicit val routeReads: Reads[RouteResource] = (
    (JsPath \ "type").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "http").readNullable[Set[HttpBackendResource]] and
      (JsPath \ "kafka").readNullable[Set[KafkaBackendResource]]
    )((routeType, name, http, kafka) => RouteResource(routeType, name, http.getOrElse(Set.empty), kafka.getOrElse(Set.empty)))


  def apply(routeType: String, name: String): RouteResource = new RouteResource(routeType, name, Set.empty, Set.empty)

  def apply(routeType: String, name: String, http: Set[HttpBackendResource]): RouteResource = {
    new RouteResource(routeType, name, http, Set.empty)
  }

  def apply(routeType: String, name: String,
            http: Set[HttpBackendResource],
            kafka: Set[KafkaBackendResource]
           ): RouteResource = new RouteResource(routeType, name, http, kafka)

}
