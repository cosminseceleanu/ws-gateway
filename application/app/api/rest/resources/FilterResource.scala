package api.rest.resources

import play.api.libs.json.{Json, OFormat}

case class FilterResource(
                           whitelistIps: Set[String],
                           blacklistIps: Set[String],
                           whitelistHosts: Set[String],
                           blacklistHosts: Set[String],
                         )

object FilterResource {
  implicit val format: OFormat[FilterResource] = Json.format[FilterResource]

  def apply(): FilterResource = new FilterResource(Set.empty, Set.empty, Set.empty, Set.empty)
  def apply(whitelistIps: Set[String], blacklistIps: Set[String],
            whitelistHosts: Set[String], blacklistHosts: Set[String]): FilterResource = new FilterResource(whitelistIps, blacklistIps,
                                                                                                                                                                 whitelistHosts, blacklistHosts)
}