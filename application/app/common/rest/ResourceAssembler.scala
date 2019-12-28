package common.rest

trait ResourceAssembler[T, R] {
  def toModel(resources: Seq[R]): Seq[T] = resources.map(r => toModel(r))
  def toModel(resource: R): T

  def toResource(models: Seq[T]): Seq[R] = models.map(m => toResource(m))
  def toResource(model: T): R
}
