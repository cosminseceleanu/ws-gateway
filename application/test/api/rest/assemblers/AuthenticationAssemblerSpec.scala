package api.rest.assemblers

import org.scalatest.Matchers._
import api.rest.resources.AuthenticationResource
import common.UnitSpec
import domain.exceptions.AuthenticationNotSupportedException
import domain.model.Authentication

class AuthenticationAssemblerSpec extends UnitSpec {
  "resource to model" when {
    "basic auth is transformed to model" should {
      "should have correct fields" in {
        val resource = AuthenticationResource.basic("user", "pass")
        val assembler = new AuthenticationAssembler()
        val model = assembler.toModel(resource)

        model.isInstanceOf[Authentication.Basic] shouldBe true
        model.asInstanceOf[Authentication.Basic].username shouldBe "user"
        model.asInstanceOf[Authentication.Basic].password shouldBe "pass"
      }
    }

    "basic auth without password is transformed to model" should {
      "password is null" in {
        val resource = AuthenticationResource.basic("user", null)
        val assembler = new AuthenticationAssembler()
        val model = assembler.toModel(resource)

        model.isInstanceOf[Authentication.Basic] shouldBe true
        model.asInstanceOf[Authentication.Basic].password shouldBe null
      }
    }

    "bearer auth is transformed to model" should {
      "should have correct fields" in {
        val resource = AuthenticationResource.bearer("url")
        val assembler = new AuthenticationAssembler()
        val model = assembler.toModel(resource)

        model.isInstanceOf[Authentication.Bearer] shouldBe true
        model.asInstanceOf[Authentication.Bearer].verifyTokenUrl shouldBe "url"
      }
    }

    "no auth is transformed to model" should {
      "should have correct fields" in {
        val resource = AuthenticationResource.none()
        val assembler = new AuthenticationAssembler()
        val model = assembler.toModel(resource)

        model.isInstanceOf[Authentication.None] shouldBe true
      }
    }

    "auth is not supported" should {
      "an exception is thrown" in {
        val resource = AuthenticationResource("aaa", None.orNull, None.orNull, None.orNull)
        val assembler = new AuthenticationAssembler()

        an[AuthenticationNotSupportedException] must be thrownBy assembler.toModel(resource)
      }
    }
  }

  "model to resource" when {
    "basic auth is transformed to resource" should {
      "should have correct fields" in {
        val model = Authentication.Basic("u", "p")
        val assembler = new AuthenticationAssembler()
        val resource = assembler.toResource(model)

        resource shouldBe AuthenticationResource.basic("u", "p")
      }
    }

    "bearer auth is transformed to resource" should {
      "should have correct fields" in {
        val model = Authentication.Bearer("url")
        val assembler = new AuthenticationAssembler()
        val resource = assembler.toResource(model)

        resource shouldBe AuthenticationResource.bearer("url")
      }
    }

    "no auth is transformed to resource" should {
      "should have correct fields" in {
        val model = Authentication.None()
        val assembler = new AuthenticationAssembler()
        val resource = assembler.toResource(model)

        resource shouldBe AuthenticationResource.none()
      }
    }
  }
}
