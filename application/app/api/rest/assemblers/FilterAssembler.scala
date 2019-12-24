package api.rest.assemblers

import api.rest.FilterResource
import common.ResourceAssembler
import domain.model.Filter
import domain.model.filters.WhitelistHosts
import javax.inject.Singleton

@Singleton
class FilterAssembler extends ResourceAssembler[Filter, FilterResource] {
  override def toModel(resource: FilterResource): Filter = WhitelistHosts(Set.empty)

  override def toResource(model: Filter): FilterResource = FilterResource("test", "aaaa")
}
