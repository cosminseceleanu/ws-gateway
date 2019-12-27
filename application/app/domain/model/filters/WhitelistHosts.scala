package domain.model.filters

import domain.model.{Filter, SetValue}

case class WhitelistHosts(whitelist: Set[String]) extends Filter {
  override val name: String = Filter.WHITELIST_HOSTS
  override val value: SetValue = SetValue(whitelist)
  override val filter: Map[String, String] => Boolean = headers => headers.contains("host") && whitelist.contains(headers("host"))
}
