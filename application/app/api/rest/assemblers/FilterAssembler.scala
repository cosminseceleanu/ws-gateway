package api.rest.assemblers

import api.rest.resources.FilterResource
import common.rest.ResourceAssembler
import domain.model.filters.{BlacklistHosts, BlacklistIps, WhitelistHosts, WhitelistIps}
import domain.model.Filter
import javax.inject.Singleton

import scala.collection.mutable

@Singleton
class FilterAssembler extends ResourceAssembler[Set[Filter], FilterResource] {

  override def toModel(resource: FilterResource): Set[Filter] = {
    val filters = mutable.Set.empty[Filter]
    if (resource.blacklistIps.nonEmpty) {
      filters.add(Filter.blacklistIps(resource.blacklistIps))
    }
    if (resource.whitelistIps.nonEmpty) {
      filters.add(Filter.whitelistIps(resource.whitelistIps))
    }
    if (resource.whitelistHosts.nonEmpty) {
      filters.add(Filter.whitelistHosts(resource.whitelistHosts))
    }
    if (resource.blacklistHosts.nonEmpty) {
      filters.add(Filter.blacklistHosts(resource.blacklistHosts))
    }

    filters.toSet
  }

  override def toResource(model: Set[Filter]): FilterResource = {
    var resource = FilterResource()
    model.foreach(f => {
      f.name match {
        case Filter.WHITELIST_IPS => resource = resource.copy(whitelistIps = f.asInstanceOf[WhitelistIps].value.asSet)
        case Filter.BLACKLIST_IPS => resource = resource.copy(blacklistIps = f.asInstanceOf[BlacklistIps].value.asSet)
        case Filter.WHITELIST_HOSTS => resource = resource.copy(whitelistHosts = f.asInstanceOf[WhitelistHosts].value.asSet)
        case Filter.BLACKLIST_HOSTS => resource = resource.copy(blacklistHosts = f.asInstanceOf[BlacklistHosts].value.asSet)
      }
    })

    resource
  }
}
