package domain.model

import common.validation.constraints.TraversableSize
import javax.validation.constraints.{Max, Min, NotNull}

import scala.annotation.meta.field

sealed trait BackendSettings {}

case class EmptySettings() extends BackendSettings

case class HttpSettings(
                         @(NotNull @field) @(TraversableSize @field)(max = 255) additionalHeaders: Map[String, String],
                         @(Min @field)(10) @(Max @field)(600000) timeoutInMillis: Int
                       ) extends BackendSettings
