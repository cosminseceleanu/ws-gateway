package api.rest.resources

case class EndpointResource(
                       id: String,
                       path: String,
                       filters: Set[FilterResource],
                       routes: Set[RouteResource]
                       )
