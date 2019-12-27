package domain.model.filters

import domain.model.{Filter, SetValue}

case class BlacklistHosts(blacklist: Set[String]) extends Filter {
  override val name: String = Filter.BLACKLIST_HOSTS
  override val value: SetValue = SetValue(blacklist)
  override val filter: Map[String, String] => Boolean = headers => !headers.contains("host") || !blacklist.contains(headers("host"))
}
