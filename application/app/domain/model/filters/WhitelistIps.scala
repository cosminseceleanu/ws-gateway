package domain.model.filters

import domain.model.Filter

case class WhitelistIps(whitelist: Set[String]) extends Filter {
  override val name: String = Filter.WHITELIST_IPS
  override val filter: Map[String, String] => Boolean = headers => whitelist.contains(headers("ip"))
}
