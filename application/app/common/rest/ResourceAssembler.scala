package common.rest

trait ResourceAssembler[T, R] {
  def toModelsSet(resources: Set[R]): Set[T] = resources.map(r => toModel(r))
  def toModelsSeq(resources: Seq[R]): Seq[T] = resources.map(r => toModel(r))
  def toModel(resource: R): T

  def toResourcesSet(models: Set[T]): Set[R] = models.map(m => toResource(m))
  def toResourcesSeq(models: Seq[T]): Seq[R] = models.map(m => toResource(m))
  def toResource(model: T): R
}
