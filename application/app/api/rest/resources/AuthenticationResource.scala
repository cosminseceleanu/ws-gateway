package api.rest.resources

import play.api.libs.json.{Json, OFormat}

case class AuthenticationResource(
                                   mode: String,
                                   username: Option[String],
                                   password: Option[String],
                                   verifyTokenUrl: Option[String])

object AuthenticationResource {
  val MODE_NONE = "none"
  val MODE_BASIC = "basic"
  val MODE_BEARER = "bearer"

  def none(): AuthenticationResource = AuthenticationResource(MODE_NONE, Option.empty, Option.empty, Option.empty)
  def basic(username: String, password: String): AuthenticationResource = AuthenticationResource(
    MODE_BASIC, Some(username), Some(password), Option.empty
  )

  def bearer(verifyTokenUrl: String): AuthenticationResource = AuthenticationResource(
    MODE_BEARER, Option.empty, Option.empty, Some(verifyTokenUrl)
  )

  implicit val format: OFormat[AuthenticationResource] = Json.format[AuthenticationResource]
}
