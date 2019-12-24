package domain.model.filters

import domain.model.Filter

case class BlacklistHosts(blacklist: Set[String]) extends Filter {
  override val name: String = Filter.BLACKLIST_HOSTS
  override val filter: Map[String, String] => Boolean = headers => !blacklist.contains(headers("host"))
}
