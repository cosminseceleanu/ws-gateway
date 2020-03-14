package domain.model.filters

import common.validation.constraints.TraversableSize
import domain.model.{Filter, SetValue}
import javax.validation.constraints.NotNull

import scala.annotation.meta.field

case class WhitelistHosts(
                           @(NotNull @field) @(TraversableSize @field)(max = 255) whitelist: Set[String]
                         ) extends Filter {
  override val name: String = Filter.WHITELIST_HOSTS
  override val value: SetValue = SetValue(whitelist)
  override val filter: Map[String, String] => Boolean = headers => headers.contains("Host") && whitelist.contains(headers("Host"))
}
