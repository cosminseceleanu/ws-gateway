package api.rest

case class FilterResource(
                           whitelistIps: Seq[String],
                           blacklistIps: Seq[String],
                           whitelistHosts: Seq[String],
                           blacklistHosts: Seq[String],
                         )
