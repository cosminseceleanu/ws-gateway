package domain.model.filters

import domain.model.{Filter, SetValue}

case class BlacklistIps(blacklist: Set[String]) extends Filter {
  override val name: String = Filter.BLACKLIST_IPS
  override val value: SetValue = SetValue(blacklist)
  override val filter: Map[String, String] => Boolean = headers => !headers.contains("ip") || !blacklist.contains(headers("ip"))
}
