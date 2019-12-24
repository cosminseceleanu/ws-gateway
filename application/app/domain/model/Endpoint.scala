package domain.model

import domain.model.AuthenticationMode.AuthenticationMode

case class Endpoint(id: String, path: String, filters: Set[Filter], routes: Set[Route], authenticationMode: AuthenticationMode)
