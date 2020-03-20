package domain.model

import common.UnitSpec

class AuthenticationSpec extends UnitSpec {

  "Basic Authentication" when {
    "username is null" should {
      "be invalid" in {
        val auth = Authentication.Basic(None.orNull, "ppppp")
        val violations = auth.getViolations(auth)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "username"
        violations.head.message mustEqual "must not be blank"
      }
    }

    "password is empty null" should {
      "be invalid" in {
        val auth = Authentication.Basic("dasdasda", None.orNull)
        val violations = auth.getViolations(auth)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "password"
        violations.head.message mustEqual "must not be blank"
      }
    }

    "password is has 4 characters" should {
      "be invalid" in {
        val auth = Authentication.Basic("dasdasda", "pp")
        val violations = auth.getViolations(auth)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "password"
      }
    }
  }

  "Bearer Authentication" when {
    "verifyToken is not url" should {
      "be invalid" in {
        val auth = Authentication.Bearer("invalid url")
        val violations = auth.getViolations(auth)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "authorizationServerUrl"
      }
    }

    "verifyToken is url" should {
      "be invalid" in {
        val auth = Authentication.Bearer(None.orNull)
        val violations = auth.getViolations(auth)

        violations.size mustEqual 1
        violations.head.propertyPath mustEqual "authorizationServerUrl"
      }
    }

    "verifyToken is url" should {
      "be valid" in {
        val auth = Authentication.Bearer("http://localhost;8080")
        val violations = auth.getViolations(auth)

        violations.size mustEqual 0
      }
    }
  }

}
