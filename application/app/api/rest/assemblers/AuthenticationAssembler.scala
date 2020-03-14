package api.rest.assemblers

import api.rest.resources.AuthenticationResource
import common.rest.ResourceAssembler
import domain.exceptions.AuthenticationNotSupportedException
import domain.model.Authentication
import domain.model.Authentication.{Basic, Bearer, None}
import javax.inject.Singleton

@Singleton
class AuthenticationAssembler extends ResourceAssembler[Authentication, AuthenticationResource] {
  override def toModel(resource: AuthenticationResource): Authentication = {
    resource.mode match {
      case AuthenticationResource.MODE_BASIC => Authentication.Basic(resource.username.orNull, resource.password.orNull)
      case AuthenticationResource.MODE_NONE => Authentication.None()
      case AuthenticationResource.MODE_BEARER => Authentication.Bearer(resource.verifyTokenUrl.orNull)
      case _ => throw AuthenticationNotSupportedException(s"Authentication mode ${resource.mode} is not supported")
    }
  }

  override def toResource(model: Authentication): AuthenticationResource = {
    model match {
      case Basic(username, password) => AuthenticationResource.basic(username, password)
      case Bearer(verifyTokenUrl) => AuthenticationResource.bearer(verifyTokenUrl)
      case None() => AuthenticationResource.none()
    }
  }
}
