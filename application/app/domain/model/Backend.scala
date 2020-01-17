package domain.model

import domain.model.BackendType.BackendType

case class Backend(backendType: BackendType, destination: String)

object Backend {
  def blackHole(): Backend = Backend(BackendType.BLACK_HOLE, "black hole")
}
