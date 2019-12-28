package domain.model

import domain.model.filters.{BlacklistHosts, BlacklistIps, WhitelistHosts, WhitelistIps}

trait Filter {
  val name: String
  val value: FilterValue
  val filter: Map[String, String] => Boolean

  def isAllowed(headers: Map[String, String]): Boolean = filter(headers)

  override def hashCode(): Int = {
    val prime = 31
    var result = 1
    result = prime * result + (if (name == null) 0 else name.hashCode)
    result
  }

  override def equals(that: Any): Boolean = {
    that match {
      case that: Route => {
        that.canEqual(this) &&
        this.name == that.name
      }
      case _ => false
    }
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Filter]
}

sealed trait FilterValue
case class StringValue(asString: String) extends FilterValue
case class NumberValue(asInt: Int) extends FilterValue
case class SetValue(asSet: Set[String]) extends FilterValue

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
