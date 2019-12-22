package api.rest.assemblers

import api.rest.FilterResource
import common.ResourceAssembler
import domain.model.Filter
import javax.inject.Singleton

@Singleton
class FilterAssembler extends ResourceAssembler[Filter, FilterResource] {
  override def toModel(resource: FilterResource): Filter = new Filter {}

  override def toResource(model: Filter): FilterResource = FilterResource("test", "aaaa")
}
