package domain.model.filters

import domain.model.Filter

case class BlacklistIps(blacklist: Set[String]) extends Filter {
  override val name: String = Filter.BLACKLIST_IPS
  override val filter: Map[String, String] => Boolean = headers => !blacklist.contains(headers("ip"))
}
