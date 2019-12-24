package domain.model

import domain.model.filters.{BlacklistHosts, BlacklistIps, WhitelistHosts, WhitelistIps}

trait Filter {
  val name: String
  val filter: Map[String, String] => Boolean

  def isValid(headers: Map[String, String]): Boolean = filter(headers)
}

object Filter {
  val WHITELIST_IPS = "whitelistIps"
  val BLACKLIST_IPS = "blacklistIps"

  val WHITELIST_HOSTS = "whitelistHosts"
  val BLACKLIST_HOSTS = "blacklistHosts"

  def whitelistIps(whitelist: Set[String]): Filter = WhitelistIps(whitelist)
  def blacklistIps(blacklist: Set[String]): Filter = BlacklistIps(blacklist)

  def whitelistHosts(whitelist: Set[String]): Filter = WhitelistHosts(whitelist)
  def blacklistHosts(blacklist: Set[String]): Filter = BlacklistHosts(blacklist)
}