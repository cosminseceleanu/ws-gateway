package common

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, SystemMaterializer}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import play.api.test.Helpers._

import scala.concurrent.Future

class AkkaPlaygroundSpec extends UnitSpec {
  implicit val system = ActorSystem("testActorSystem")
  implicit val materializer = SystemMaterializer(system)

  "Akka streams" in {
    val runnableGraph = Source(1 to 6).via(Flow[Int].map(_ * 2)).to(Sink.foreach(println(_)))

    val res = runnableGraph.run()
  }

  "Akka streams async boundary" in {
    Source(List(1, 2, 3))
      .map(_ + 1)
      .async.map(_ * 2)
      .to(Sink.foreach(println(_)))
      .run()
  }
}
