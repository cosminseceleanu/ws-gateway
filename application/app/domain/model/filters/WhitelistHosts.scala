package domain.model.filters

import domain.model.Filter

case class WhitelistHosts(whitelist: Set[String]) extends Filter {
  override val name: String = Filter.WHITELIST_HOSTS
  override val filter: Map[String, String] => Boolean = headers => whitelist.contains(headers("host"))
}
