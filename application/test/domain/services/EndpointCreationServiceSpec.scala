package domain.services

import common.UnitSpec
import domain.model.{Endpoint, Route}
import domain.repositories.EndpointRepository
import play.api.test.Helpers._

import scala.concurrent.Future

class EndpointCreationServiceSpec extends UnitSpec {
  private val repoMock = mock[EndpointRepository]
  private val subject = new EndpointCreationService(repoMock)

  "Create Endpoint" when {
    "has all required values" should {
      "be saved" in {
        val endpoint = Endpoint(null, "/a", Set.empty, Set(Route.connect(), Route.default(), Route.disconnect()))
        val expected = endpoint.copy(id = "a")
        (repoMock.save _).expects(endpoint).returning(Future.successful(expected)).once()

        val result = await(subject.create(endpoint))

        result.id != null mustBe true
        result.id mustBe "a"
        result.routes must have size(3)
        result.filters mustBe empty
      }
    }

    "has only connect route" should {
      "add the missing routes and then is saved" in {
        val endpoint = Endpoint(null, "/b", Set.empty, Set(Route.connect()))
        (repoMock.save _).expects(*).onCall {
          e: Endpoint => Future.successful(e.copy(id = "b"))
        }
        val result = await(subject.create(endpoint))

        result.routes must have size(3)
        result.routes must contain(Route.connect())
        result.routes must contain(Route.disconnect())
        result.routes must contain(Route.default())
      }
    }
  }

}
