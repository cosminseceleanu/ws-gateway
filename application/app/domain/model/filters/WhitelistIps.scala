package domain.model.filters

import common.validation.constraints.TraversableSize
import domain.model.{Filter, SetValue}
import javax.validation.constraints.NotNull
import play.mvc.Http.HeaderNames.X_FORWARDED_FOR

import scala.annotation.meta.field

case class WhitelistIps(
                         @(NotNull @field) @(TraversableSize @field)(max = 255) whitelist: Set[String]
                       ) extends Filter {
  override val name: String = Filter.WHITELIST_IPS
  override val value: SetValue = SetValue(whitelist)
  override val filter: Map[String, String] => Boolean = {
    headers => headers.contains(X_FORWARDED_FOR) && whitelist.contains(headers(X_FORWARDED_FOR))
  }
}
