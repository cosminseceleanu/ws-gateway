package domain.model.filters

import domain.model.{Filter, SetValue}

case class WhitelistIps(whitelist: Set[String]) extends Filter {
  override val name: String = Filter.WHITELIST_IPS
  override val value: SetValue = SetValue(whitelist)
  override val filter: Map[String, String] => Boolean = headers => headers.contains("ip") && whitelist.contains(headers("ip"))
}
