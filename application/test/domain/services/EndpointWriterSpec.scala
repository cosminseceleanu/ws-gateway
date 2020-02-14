package domain.services

import common.UnitSpec
import common.validation.exceptions.ConstraintViolationException
import domain.model.{Endpoint, Route}
import domain.repositories.EndpointRepository
import play.api.test.Helpers._

import scala.concurrent.Future

class EndpointWriterSpec extends UnitSpec {
  private val repoMock = mock[EndpointRepository]
  private val providerMock = mock[EndpointsProvider]
  private val subject = new EndpointWriter(repoMock, providerMock)

  "Create Endpoint" when {
    "has all required values" should {
      "be saved" in {
        val endpoint = Endpoint(None.orNull, "/a", Set.empty, Set(Route.connect(), Route.default(), Route.disconnect()))
        val expected = endpoint.copy(id = "a")
        (repoMock.create _).expects(endpoint).returning(Future.successful(expected)).once()

        val result = await(subject.create(endpoint))

        result.id != null mustBe true
        result.id mustBe "a"
        result.routes must have size(3)
        result.filters mustBe empty
      }
    }

    "has only connect route" should {
      "add the missing routes and then is saved" in {
        val endpoint = Endpoint(None.orNull, "/b", Set.empty, Set(Route.connect()))
        (repoMock.create _).expects(*).onCall {
          e: Endpoint => Future.successful(e.copy(id = "b"))
        }
        val result = await(subject.create(endpoint))

        result.routes must have size(3)
        result.routes must contain(Route.connect())
        result.routes must contain(Route.disconnect())
        result.routes must contain(Route.default())
      }
    }

    "endpoint is invalid" should {
      "an exception is thrown" in {
        val endpoint = Endpoint(None.orNull, "/api/internal/adasda", None.orNull, Set(Route.connect()))

        an[ConstraintViolationException] must be thrownBy (await(subject.create(endpoint)))
      }
    }
  }

  "Update Endpoint" when {
    "endpoint is invalid" should {
      "an exception is thrown" in {
        val initial = Endpoint("id", "/a", Set.empty, Set(Route.connect(), Route.default(), Route.disconnect()))
        (providerMock.get _).expects("id").returning(Future.successful(initial)).once()
        val endpoint = Endpoint(None.orNull, "/api/internal/adasda", None.orNull, Set(Route.connect()))

        an[ConstraintViolationException] must be thrownBy (await(subject.update("id", endpoint)))
      }
    }
  }
}
