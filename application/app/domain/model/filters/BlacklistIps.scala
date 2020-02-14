package domain.model.filters

import common.validation.constraints.TraversableSize
import domain.model.{Filter, SetValue}
import javax.validation.constraints.NotNull

import scala.annotation.meta.field

case class BlacklistIps(
                         @(NotNull @field) @(TraversableSize @field)(max = 255) blacklist: Set[String]
                       ) extends Filter {
  override val name: String = Filter.BLACKLIST_IPS
  override val value: SetValue = SetValue(blacklist)
  override val filter: Map[String, String] => Boolean = headers => !headers.contains("ip") || !blacklist.contains(headers("ip"))
}
