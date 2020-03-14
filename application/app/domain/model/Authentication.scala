package domain.model

import common.validation.Validatable
import javax.validation.constraints.{NotBlank, Size, NotNull}
import org.hibernate.validator.constraints.URL

import scala.annotation.meta.field

sealed trait Authentication extends Validatable {

}

object Authentication {
  case class None() extends Authentication

  case class Basic(
                    @(NotBlank @field) @(Size @field)(min = 5, max = 255) username: String,
                    @(NotBlank @field) @(Size @field)(min = 5, max = 255) password: String
                  ) extends Authentication

  case class Bearer(
                     @(NotNull @field) @(URL @field) @(Size @field)(min = 5, max = 255) verifyTokenUrl: String
                   ) extends Authentication
}
