package api.rest.resources

case class EndpointResource(
                       id: String,
                       path: String,
                       filters: FilterResource,
                       routes: Set[RouteResource],
                       authenticationMode: String)
