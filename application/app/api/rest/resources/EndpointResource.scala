package api.rest.resources

import play.api.libs.json.{Format, JsNull, JsResult, JsValue, Json, OFormat, Writes}

case class EndpointResource(
                       id: Option[String],
                       path: String,
                       backendParallelism: Option[Int],
                       bufferSize: Option[Int],
                       filters: FilterResource,
                       routes: Set[RouteResource],
                       authenticationMode: String) {

  def httpBackends: List[HttpBackendResource] = routes.toList.flatMap(_.http)
}

object EndpointResource {
  implicit def optionFormat[T: Format]: Format[Option[T]] = new Format[Option[T]]{
    override def reads(json: JsValue): JsResult[Option[T]] = json.validateOpt[T]

    override def writes(o: Option[T]): JsValue = o match {
      case Some(t) => implicitly[Writes[T]].writes(t)
      case None => JsNull
    }
  }
  implicit val format: OFormat[EndpointResource] = Json.format[EndpointResource]
}
